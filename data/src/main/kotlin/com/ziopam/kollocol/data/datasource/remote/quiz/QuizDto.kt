package com.ziopam.kollocol.data.datasource.remote.quiz

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.ziopam.kollocol.data.storage.room.QuizInstanceEntity

data class GetParticipatingInstancesResponseDto(
    @SerializedName("instances")
    val instances: List<ParticipatingInstanceDto>
)

data class ParticipatingInstanceDto(
    @SerializedName("instance")
    val instance: InstanceDto,
    
    @SerializedName("session_status")
    val sessionStatus: String
)

data class GetHostingInstancesResponseDto(
    @SerializedName("instances")
    val instances: List<InstanceDto>
)

data class InstanceDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("access_code")
    val accessCode: String,
    
    @SerializedName("quiz_type")
    val quizType: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("total_questions")
    val totalQuestions: Int,
    
    @SerializedName("total_time")
    val totalTime: Int? = null,
    
    @SerializedName("deadline")
    val deadline: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String
) {

    fun toEntity(type: String): QuizInstanceEntity {
        return QuizInstanceEntity(
            id = id,
            title = title,
            accessCode = accessCode,
            quizType = quizType,
            totalQuestions = totalQuestions,
            totalTime = totalTime,
            deadline = deadline,
            instanceType = type,
            status = status
        )
    }
}

data class GetTemplatesResponseDto(
    @SerializedName("templates")
    val templates: List<TemplateDTO>
)

data class TemplateDTO(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("user_id")
    val userId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("quiz_type")
    val quizType: String,
    
    @SerializedName("settings")
    val settings: QuizSettingsDTO?,
    
    @SerializedName("questions")
    val questions: List<QuestionDTO>,
    
    @SerializedName("total_questions")
    val totalQuestions: Int,
    
    @SerializedName("total_time")
    val totalTime: Int
)

data class QuizSettingsDTO(
    @SerializedName("time_limit_total")
    val timeLimitTotal: Int?,
    
    @SerializedName("random_order")
    val randomOrder: Boolean?,
    
    @SerializedName("show_correct_answers")
    val showCorrectAnswers: Boolean?,
    
    @SerializedName("allow_review")
    val allowReview: Boolean?
)

// --- Create Template Request/Response DTOs ---

data class CreateTemplateRequestDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("quiz_type")
    val quizType: String,

    @SerializedName("questions")
    val questions: List<QuestionInputDto>,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("settings")
    val settings: QuizSettingsDTO? = null
)

data class QuestionInputDto(
    @SerializedName("text")
    val text: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("correct_answer")
    val correctAnswer: String,

    @SerializedName("max_score")
    val maxScore: Int,

    @SerializedName("options")
    val options: List<String>? = null,

    @SerializedName("time_limit_sec")
    val timeLimitSec: Int? = null,

    @SerializedName("order_index")
    val orderIndex: Int
)

data class CreateTemplateResponseDto(
    @SerializedName("template_id")
    val templateId: String
)

data class QuestionDTO(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("text")
    val text: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("options")
    val options: List<String>?,
    
    @SerializedName("correct_answer")
    @JsonAdapter(CorrectAnswerDeserializer::class)
    val correctAnswer: String,
    
    @SerializedName("max_score")
    val maxScore: Int,
    
    @SerializedName("time_limit_sec")
    val timeLimitSec: Int?,
    
    @SerializedName("order_index")
    val orderIndex: Int,
    
    @SerializedName("ai_answer")
    val aiAnswer: String?
)