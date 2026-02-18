package com.ziopam.kollocol.data.datasource.remote.quiz

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
            instanceType = type
        )
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        return if (minutes > 0) {
            "$minutes мин"
        } else {
            "$seconds сек"
        }
    }
}