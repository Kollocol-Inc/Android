package com.ziopam.kollocol.data.datasource.remote.user

import com.google.gson.annotations.SerializedName
import com.ziopam.kollocol.domain.model.NotificationSettings
import com.ziopam.kollocol.domain.model.User

data class RegisterUserRequestDto(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)

data class RegisterUserResponseDto(
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String? = null
)

data class GetUserResponseDto(
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String? = null
) {
    fun toUser(): User {
        return User(
            avatarUrl = avatarUrl,
            firstName = firstName.orEmpty(),
            lastName = lastName.orEmpty(),
            email = email.orEmpty()
        )
    }
}

data class UpdateProfileRequestDto(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)

data class UpdateUserRequestDto(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)

data class NotificationSettingsDto(
    @SerializedName("new_quizzes")
    val newQuizzes: Boolean = true,

    @SerializedName("quiz_results")
    val quizResults: Boolean = true,

    @SerializedName("group_invites")
    val groupInvites: Boolean = true,

    @SerializedName("deadline_reminder")
    val deadlineReminder: String = "never"
) {
    fun toNotificationSettings(): NotificationSettings = NotificationSettings(
        newQuizzes = newQuizzes,
        quizResults = quizResults,
        groupInvites = groupInvites,
        deadlineReminder = deadlineReminder
    )
}

data class UpdateNotificationSettingsRequestDto(
    @SerializedName("new_quizzes")
    val newQuizzes: Boolean,

    @SerializedName("quiz_results")
    val quizResults: Boolean,

    @SerializedName("group_invites")
    val groupInvites: Boolean,

    @SerializedName("deadline_reminder")
    val deadlineReminder: String
)
