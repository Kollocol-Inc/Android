package com.ziopam.kollocol.data.storage.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ziopam.kollocol.core.common.TimeFormatter
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode

@Entity(tableName = "quiz_instances")
data class QuizInstanceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val accessCode: String,
    val quizType: String,
    val totalQuestions: Int,
    val totalTime: Int?,
    val deadline: String?,
    val instanceType: String, // "participating" or "hosting"
    val status: String? = null
)

fun QuizInstanceEntity.toQuizInfo(): QuizInfo {
    return QuizInfo(
        title = title,
        accessCode = accessCode,
        totalQuestions = totalQuestions,
        mode = when (quizType.lowercase()) {
            "sync" -> QuizMode.SYNC
            "async" -> QuizMode.ASYNC
            else -> QuizMode.ASYNC
        },
        deadline = deadline,
        totalTime = totalTime?.let { TimeFormatter.formatTime(it) }
    )
}