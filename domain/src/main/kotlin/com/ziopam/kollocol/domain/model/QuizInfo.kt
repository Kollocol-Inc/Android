package com.ziopam.kollocol.domain.model

data class QuizInfo(
    val title: String,
    val accessCode: String,
    val totalQuestions: Int,
    val mode: QuizMode,
    val deadline: String? = null,
    val totalTime: String? = null
)

enum class QuizMode {
    SYNC,
    ASYNC
}