package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.quiz.CreateInstanceRequestDto
import com.ziopam.kollocol.data.datasource.remote.quiz.CreateTemplateRequestDto
import com.ziopam.kollocol.data.datasource.remote.quiz.GenerateQuestionsRequestDto
import com.ziopam.kollocol.data.datasource.remote.quiz.GenerateTemplateRequestDto
import com.ziopam.kollocol.data.datasource.remote.quiz.GradeAnswerRequestDto
import com.ziopam.kollocol.data.datasource.remote.quiz.QuestionInputDto
import com.ziopam.kollocol.data.datasource.remote.quiz.QuizApi
import com.ziopam.kollocol.data.datasource.remote.quiz.QuizSettingsDTO
import com.ziopam.kollocol.data.datasource.remote.quiz.TemplateDTO
import com.ziopam.kollocol.data.storage.room.QuizInstanceDao
import com.ziopam.kollocol.data.storage.room.TemplateDao
import com.ziopam.kollocol.data.storage.room.TemplateEntity
import com.ziopam.kollocol.data.storage.room.toEntity
import com.ziopam.kollocol.data.storage.room.toQuizInfo
import com.ziopam.kollocol.domain.model.ParticipantAnswers
import com.ziopam.kollocol.domain.model.QuizInstanceDetails
import com.ziopam.kollocol.domain.model.QuizParticipant
import com.ziopam.kollocol.domain.model.ReviewAnswer
import com.ziopam.kollocol.domain.model.ReviewQuestion
import com.ziopam.kollocol.domain.model.TemplateDetail
import com.ziopam.kollocol.domain.model.TemplateQuestion
import com.ziopam.kollocol.domain.repository.QuizRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val api: QuizApi,
    private val safeApiCall: SafeApiCall,
    private val quizDao: QuizInstanceDao,
    private val templateDao: TemplateDao,
) : QuizRepository {

    override val participatingQuizzes = quizDao.getQuizzesByType("participating").map { entities ->
        entities.map { it.toQuizInfo() }
    }

    override val hostingQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.map { it.toQuizInfo() }
    }

    override val runningQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.filter {it.status != "pending_review" && it.status != "reviewed"}.map { it.toQuizInfo() }
    }

    override val pendingQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.filter { it.status == "pending_review" }
            .map { it.toQuizInfo() }
    }

    override val reviewedQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.filter { it.status == "reviewed" }
            .map { it.toQuizInfo() }
    }
    
    override val templates = templateDao.getAllTemplates().map { entities ->
        entities.map { it.toQuizInfo() }
    }

    override suspend fun getParticipatingQuizzes(sessionStatus: String?): AppResult<Unit> {
        val result = safeApiCall.call {
            api.getParticipatingInstances(sessionStatus)
        }

        return when (result) {
            is AppResult.Ok -> {
                val entities = result.value.instances.map { participatingDto ->
                    participatingDto.instance.toEntity("participating")
                }
                if (sessionStatus == null) {
                    quizDao.syncQuizzesByType("participating", entities)
                } else {
                    quizDao.syncQuizzesByTypeAndStatus("participating", sessionStatus, entities)
                }
                AppResult.Ok(Unit)
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun getHostingQuizzes(status: String?): AppResult<Unit> {
        val result = safeApiCall.call {
            api.getHostingInstances(status)
        }

        return when (result) {
            is AppResult.Ok -> {
                val entities = result.value.instances.map { it.toEntity("hosting") }
                if (status == null) {
                    quizDao.syncQuizzesByType("hosting", entities)
                } else {
                    quizDao.syncQuizzesByTypeAndStatus("hosting", status, entities)
                }
                AppResult.Ok(Unit)
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }
    
    override suspend fun getTemplates(): AppResult<Unit> {
        val result = safeApiCall.call {
            api.getTemplates()
        }
        
        return when (result) {
            is AppResult.Ok -> {
                val entities = result.value.templates.map { it.toEntity() }
                templateDao.syncAllTemplates(entities)

                AppResult.Ok(Unit)
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun createTemplate(
        title: String,
        quizType: String,
        questions: List<Map<String, Any?>>,
        description: String?,
        randomOrder: Boolean
    ): AppResult<String> {
        val questionDtos = questions.map { q ->
            QuestionInputDto(
                text = q["text"] as String,
                type = q["type"] as String,
                correctAnswer = q["correct_answer"],
                maxScore = q["max_score"] as Int,
                options = q["options"] as? List<String>,
                timeLimitSec = q["time_limit_sec"] as? Int,
            )
        }

        val settings = QuizSettingsDTO(
            timeLimitTotal = null,
            randomOrder = randomOrder,
            showCorrectAnswers = null,
            allowReview = null
        )

        val request = CreateTemplateRequestDto(
            title = title,
            quizType = quizType,
            questions = questionDtos,
            description = description,
            settings = settings
        )

        val result = safeApiCall.call {
            api.createTemplate(request)
        }

        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.templateId)
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun createInstance(
        templateId: String,
        title: String,
        deadline: String?,
        groupId: String?
    ): AppResult<Unit> {
        val request = CreateInstanceRequestDto(
            templateId = templateId,
            title = title,
            deadline = deadline,
            groupId = groupId
        )
        val result = safeApiCall.call { api.createInstance(request) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(Unit)
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun getTemplateById(id: String): AppResult<TemplateDetail> {
        val cached = templateDao.getTemplateById(id)
        if (cached != null) {
            return AppResult.Ok(cached.toDetail())
        }
        val result = safeApiCall.call { api.getTemplate(id) }
        return when (result) {
            is AppResult.Ok -> {
                templateDao.insert(result.value.toEntity())
                AppResult.Ok(result.value.toDetail())
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun updateTemplate(
        id: String,
        title: String,
        quizType: String,
        questions: List<Map<String, Any?>>,
        description: String?,
        randomOrder: Boolean
    ): AppResult<Unit> {
        val questionDtos = questions.map { q ->
            QuestionInputDto(
                text = q["text"] as String,
                type = q["type"] as String,
                correctAnswer = q["correct_answer"],
                maxScore = q["max_score"] as Int,
                options = q["options"] as? List<String>,
                timeLimitSec = q["time_limit_sec"] as? Int,
            )
        }
        val settings = QuizSettingsDTO(
            timeLimitTotal = null,
            randomOrder = randomOrder,
            showCorrectAnswers = null,
            allowReview = null
        )
        val request = CreateTemplateRequestDto(
            title = title,
            quizType = quizType,
            questions = questionDtos,
            description = description,
            settings = settings
        )
        val result = safeApiCall.call { api.updateTemplate(id, request) }
        return when (result) {
            is AppResult.Ok -> {
                getTemplates()
                AppResult.Ok(Unit)
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun deleteTemplate(id: String): AppResult<Unit> {
        val result = safeApiCall.call { api.deleteTemplate(id) }
        return when (result) {
            is AppResult.Ok -> {
                templateDao.deleteTemplate(id)
                AppResult.Ok(Unit)
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun generateTemplate(prompt: String): AppResult<TemplateDetail> {
        val result = safeApiCall.call { api.generateTemplate(GenerateTemplateRequestDto(text = prompt)) }
        return when (result) {
            is AppResult.Ok -> {
                val resp = result.value
                AppResult.Ok(TemplateDetail(
                    id = "",
                    title = resp.title,
                    quizType = "async",
                    randomOrder = false,
                    questions = resp.questions.map { q ->
                        TemplateQuestion(
                            id = "",
                            text = q.text,
                            type = q.type,
                            options = q.options,
                            correctAnswer = q.correctAnswer,
                            maxScore = q.maxScore,
                            timeLimitSec = q.timeLimitSec
                        )
                    }
                ))
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun generateQuestions(templateId: String, prompt: String): AppResult<List<TemplateQuestion>> {
        val result = safeApiCall.call {
            api.generateQuestions(GenerateQuestionsRequestDto(text = prompt))
        }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.questions.map { q ->
                TemplateQuestion(
                    id = "",
                    text = q.text,
                    type = q.type,
                    options = q.options,
                    correctAnswer = q.correctAnswer,
                    maxScore = q.maxScore,
                    timeLimitSec = q.timeLimitSec
                )
            })
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    private fun TemplateDTO.toDetail(): TemplateDetail = TemplateDetail(
        id = id,
        title = title,
        quizType = quizType,
        randomOrder = settings?.randomOrder ?: false,
        questions = questions.map { q ->
            TemplateQuestion(
                id = q.id,
                text = q.text,
                type = q.type,
                options = q.options,
                correctAnswer = q.correctAnswer,
                maxScore = q.maxScore,
                timeLimitSec = q.timeLimitSec
            )
        }
    )

    override suspend fun getInstanceById(instanceId: String): AppResult<QuizInstanceDetails> {
        val result = safeApiCall.call { api.getInstanceById(instanceId) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(QuizInstanceDetails(
                title = result.value.instance.title,
                status = result.value.instance.status,
                deadline = result.value.instance.deadline
            ))
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun getInstanceParticipants(instanceId: String): AppResult<List<QuizParticipant>> {
        val result = safeApiCall.call { api.getInstanceParticipants(instanceId) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.participants.map { dto ->
                QuizParticipant(
                    userId = dto.user.id,
                    firstName = dto.user.firstName,
                    lastName = dto.user.lastName,
                    email = dto.user.email,
                    avatarUrl = dto.user.avatarUrl,
                    sessionStatus = dto.sessionStatus,
                    reviewStatus = dto.reviewStatus,
                    totalScore = dto.totalScore,
                    maxPossibleScore = dto.maxPossibleScore
                )
            })
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun getParticipantAnswers(instanceId: String, userId: String): AppResult<ParticipantAnswers> {
        val result = safeApiCall.call { api.getParticipantAnswers(instanceId, userId) }
        return when (result) {
            is AppResult.Ok -> {
                val dto = result.value
                AppResult.Ok(ParticipantAnswers(
                    instanceTitle = dto.instance.title,
                    questions = dto.questions.map { q ->
                        ReviewQuestion(
                            id = q.id,
                            text = q.text,
                            type = q.type,
                            options = q.options,
                            correctAnswer = q.correctAnswer,
                            maxScore = q.maxScore,
                            orderIndex = q.orderIndex
                        )
                    },
                    answers = dto.answers.map { a ->
                        ReviewAnswer(
                            questionId = a.questionId,
                            answer = a.answer ?: "",
                            isCorrect = a.isCorrect,
                            isReviewed = a.isReviewed,
                            score = a.score
                        )
                    }
                ))
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun gradeAnswer(
        instanceId: String,
        participantId: String,
        questionId: String,
        score: Int
    ): AppResult<Unit> {
        val result = safeApiCall.call {
            api.gradeAnswer(instanceId, GradeAnswerRequestDto(participantId, questionId, score))
        }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(Unit)
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun publishResults(instanceId: String): AppResult<Unit> {
        val result = safeApiCall.call { api.publishResults(instanceId) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(Unit)
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun deleteInstance(instanceId: String): AppResult<Unit> {
        val result = safeApiCall.call { api.deleteInstance(instanceId) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(Unit)
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    private fun TemplateEntity.toDetail(): TemplateDetail = TemplateDetail(
        id = id,
        title = title,
        quizType = quizType,
        randomOrder = settings?.randomOrder ?: false,
        questions = questions.map { q ->
            TemplateQuestion(
                id = q.id,
                text = q.text,
                type = q.type,
                options = q.options,
                correctAnswer = q.correctAnswer,
                maxScore = q.maxScore,
                timeLimitSec = q.timeLimitSec
            )
        }
    )
}