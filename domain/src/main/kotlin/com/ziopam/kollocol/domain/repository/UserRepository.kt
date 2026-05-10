package com.ziopam.kollocol.domain.repository

import android.net.Uri
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.NotificationSettings
import com.ziopam.kollocol.domain.model.User

interface UserRepository {
    suspend fun registerUser(firstName: String, lastName: String): AppResult<Unit>
    suspend fun getUser(): AppResult<User>
    suspend fun updateName(firstName: String, lastName: String): AppResult<Unit>
    suspend fun uploadAvatar(uri: Uri): AppResult<String?>
    suspend fun deleteAvatar(): AppResult<Unit>
    suspend fun deleteAccount(): AppResult<Unit>
    suspend fun getNotificationSettings(): AppResult<NotificationSettings>
    suspend fun updateNotificationSettings(settings: NotificationSettings): AppResult<NotificationSettings>
}
