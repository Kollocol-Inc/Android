package com.ziopam.kollocol.domain.model

data class QuizInfo(
    val title: String,
    val quizCode: String,
    val questionsCount: Int,
    val deadline: String? = null,
    val mode: QuizMode,
)

enum class QuizMode {
    SYNC,
    ASYNC
}