package com.ziopam.kollocol.domain.model

enum class NotificationType {
    GROUP_INVITE, QUIZ_CREATED, QUIZ_RESULTS, GRADE_CHANGED, DEADLINE_REMINDER, UNKNOWN
}

data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val createdAt: String,
    val relatedEntityId: String? = null,
    val requiresAction: Boolean = false
)
