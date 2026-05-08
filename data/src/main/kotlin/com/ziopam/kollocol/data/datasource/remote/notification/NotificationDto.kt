package com.ziopam.kollocol.data.datasource.remote.notification

import com.google.gson.annotations.SerializedName
import com.ziopam.kollocol.domain.model.Notification
import com.ziopam.kollocol.domain.model.NotificationType

data class NotificationDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("is_read")
    val isRead: Boolean,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("reference_id")
    val referenceId: String? = null
) {
    fun toDomain(): Notification = Notification(
        id = id,
        userId = userId,
        type = when (type) {
            "group_invite" -> NotificationType.GROUP_INVITE
            "quiz_created" -> NotificationType.QUIZ_CREATED
            "quiz_results" -> NotificationType.QUIZ_RESULTS
            "grade_changed" -> NotificationType.GRADE_CHANGED
            "deadline_reminder" -> NotificationType.DEADLINE_REMINDER
            else -> NotificationType.UNKNOWN
        },
        title = title,
        content = content,
        isRead = isRead,
        createdAt = createdAt,
        referenceId = referenceId
    )
}

data class GetNotificationsResponseDto(
    @SerializedName("notifications")
    val notifications: List<NotificationDto>,

    @SerializedName("total")
    val total: Int
)

data class NotificationIdsRequestDto(
    @SerializedName("ids")
    val ids: List<String>
)
