package com.ziopam.kollocol.data.repository

import android.content.Context
import android.net.Uri
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.user.RegisterUserRequestDto
import com.ziopam.kollocol.data.datasource.remote.user.UpdateNotificationSettingsRequestDto
import com.ziopam.kollocol.data.datasource.remote.user.UpdateProfileRequestDto
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.domain.model.NotificationSettings
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalRepository
import com.ziopam.kollocol.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val safeApiCall: SafeApiCall,
    private val personalRepository: PersonalRepository,
    @ApplicationContext private val context: Context
) : UserRepository {

    override suspend fun registerUser(firstName: String, lastName: String): AppResult<Unit> {
        val result = safeApiCall.call {
            api.registerUser(RegisterUserRequestDto(firstName, lastName))
        }

        if (result is AppResult.Ok) {
            personalRepository.updateUser(
                User(
                    avatarUrl = result.value.avatarUrl,
                    firstName = result.value.firstName.orEmpty(),
                    lastName = result.value.lastName.orEmpty(),
                    email = result.value.email.orEmpty()
                )
            )
        }

        return when (result) {
            is AppResult.Ok -> AppResult.Ok(Unit)
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun getUser(): AppResult<User> {
        val result = safeApiCall.call { api.getUser() }
        return when (result) {
            is AppResult.Ok -> {
                val user = result.value.toUser()
                personalRepository.updateUser(user)
                AppResult.Ok(user)
            }
            is AppResult.Err -> result
        }
    }

    override suspend fun updateName(firstName: String, lastName: String): AppResult<Unit> {
        val result = safeApiCall.call {
            api.updateProfile(UpdateProfileRequestDto(firstName, lastName))
        }
        if (result is AppResult.Ok) {
            personalRepository.updateUser(result.value.toUser())
        }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(Unit)
            is AppResult.Err -> result
        }
    }

    override suspend fun uploadAvatar(uri: Uri): AppResult<String?> {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return AppResult.Err(AppError.Unknown("Cannot open image"))

        val bytes = withContext(Dispatchers.IO) {
            inputStream.use { it.readBytes() }
        }

        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val requestBody = bytes.toRequestBody(mimeType.toMediaType())
        val part = MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestBody)

        val uploadResult = safeApiCall.call { api.uploadAvatar(part) }
        if (uploadResult is AppResult.Err) return uploadResult

        val user = (uploadResult as AppResult.Ok).value.toUser()
        personalRepository.updateUser(user)
        return AppResult.Ok(user.avatarUrl)
    }

    override suspend fun deleteAvatar(): AppResult<Unit> {
        val result = safeApiCall.call { api.deleteAvatar() }
        if (result is AppResult.Ok) {
            val userResult = safeApiCall.call { api.getUser() }
            if (userResult is AppResult.Ok) {
                personalRepository.updateUser(userResult.value.toUser())
            }
        }
        return result
    }

    override suspend fun deleteAccount(): AppResult<Unit> {
        val result = safeApiCall.call { api.deleteAccount() }
        if (result is AppResult.Ok) {
            personalRepository.clear()
        }
        return result
    }

    override suspend fun getNotificationSettings(): AppResult<NotificationSettings> {
        val result = safeApiCall.call { api.getNotificationSettings() }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.toNotificationSettings())
            is AppResult.Err -> result
        }
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings): AppResult<NotificationSettings> {
        val result = safeApiCall.call {
            api.updateNotificationSettings(
                UpdateNotificationSettingsRequestDto(
                    newQuizzes = settings.newQuizzes,
                    quizResults = settings.quizResults,
                    groupInvites = settings.groupInvites,
                    deadlineReminder = settings.deadlineReminder
                )
            )
        }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.toNotificationSettings())
            is AppResult.Err -> result
        }
    }
}
