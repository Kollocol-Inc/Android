package com.ziopam.kollocol.domain.model

data class TemplateDetail(
    val id: String,
    val title: String,
    val quizType: String,
    val randomOrder: Boolean,
    val questions: List<TemplateQuestion>
)

data class TemplateQuestion(
    val id: String,
    val text: String,
    val type: String,
    val options: List<String>?,
    val correctAnswer: String,
    val maxScore: Int,
    val timeLimitSec: Int?
)
