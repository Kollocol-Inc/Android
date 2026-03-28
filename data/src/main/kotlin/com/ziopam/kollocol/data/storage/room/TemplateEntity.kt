package com.ziopam.kollocol.data.storage.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ziopam.kollocol.core.common.TimeFormatter
import com.ziopam.kollocol.data.datasource.remote.quiz.QuestionDTO
import com.ziopam.kollocol.data.datasource.remote.quiz.QuizSettingsDTO
import com.ziopam.kollocol.data.datasource.remote.quiz.TemplateDTO
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "quiz_type")
    val quizType: String,
    
    @ColumnInfo(name = "settings")
    val settings: QuizSettingsDTO?,
    
    @ColumnInfo(name = "questions")
    val questions: List<QuestionDTO>,
    
    @ColumnInfo(name = "total_questions")
    val totalQuestions: Int,
    
    @ColumnInfo(name = "total_time")
    val totalTime: Int
)

fun TemplateEntity.toQuizInfo(): QuizInfo {
    return QuizInfo(
        id = id,
        title = title,
        accessCode = "",
        totalQuestions = totalQuestions,
        mode = if (quizType == "sync") QuizMode.SYNC else QuizMode.ASYNC,
        deadline = null,
        totalTime = TimeFormatter.formatTime(totalTime)
    )
}

fun TemplateDTO.toEntity(): TemplateEntity {
    return TemplateEntity(
        id = id,
        userId = userId,
        title = title,
        description = description,
        quizType = quizType,
        settings = settings,
        questions = questions,
        totalQuestions = totalQuestions,
        totalTime = totalTime
    )
}