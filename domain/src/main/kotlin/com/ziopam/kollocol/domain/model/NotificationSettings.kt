package com.ziopam.kollocol.domain.model

data class NotificationSettings(
    val newQuizzes: Boolean,
    val quizResults: Boolean,
    val groupInvites: Boolean,
    val deadlineReminder: String
)
