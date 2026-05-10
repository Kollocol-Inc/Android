package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.ParticipantAnswers
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizInstanceDetails
import com.ziopam.kollocol.domain.model.QuizParticipant
import com.ziopam.kollocol.domain.model.TemplateDetail
import com.ziopam.kollocol.domain.model.TemplateQuestion
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    val participatingQuizzes: Flow<List<QuizInfo>>
    val hostingQuizzes: Flow<List<QuizInfo>>
    val runningQuizzes: Flow<List<QuizInfo>>
    val pendingQuizzes: Flow<List<QuizInfo>>
    val reviewedQuizzes: Flow<List<QuizInfo>>
    val templates: Flow<List<QuizInfo>>

    suspend fun getParticipatingQuizzes(sessionStatus: String? = null): AppResult<Unit>
    suspend fun getHostingQuizzes(status: String? = null): AppResult<Unit>
    suspend fun getTemplates(): AppResult<Unit>
    suspend fun createTemplate(
        title: String,
        quizType: String,
        questions: List<Map<String, Any?>>,
        description: String? = null,
        randomOrder: Boolean = false
    ): AppResult<String>

    suspend fun createInstance(
        templateId: String,
        title: String,
        deadline: String? = null,
        groupId: String? = null
    ): AppResult<Unit>

    suspend fun getTemplateById(id: String): AppResult<TemplateDetail>

    suspend fun updateTemplate(
        id: String,
        title: String,
        quizType: String,
        questions: List<Map<String, Any?>>,
        description: String? = null,
        randomOrder: Boolean = false
    ): AppResult<Unit>

    suspend fun deleteTemplate(id: String): AppResult<Unit>

    suspend fun generateTemplate(prompt: String): AppResult<TemplateDetail>

    suspend fun generateQuestions(templateId: String, prompt: String): AppResult<List<TemplateQuestion>>

    suspend fun getInstanceById(instanceId: String): AppResult<QuizInstanceDetails>

    suspend fun getInstanceParticipants(instanceId: String): AppResult<List<QuizParticipant>>

    suspend fun getParticipantAnswers(instanceId: String, userId: String): AppResult<ParticipantAnswers>

    suspend fun gradeAnswer(
        instanceId: String,
        participantId: String,
        questionId: String,
        score: Int
    ): AppResult<Unit>

    suspend fun publishResults(instanceId: String): AppResult<Unit>

    suspend fun deleteInstance(instanceId: String): AppResult<Unit>
}