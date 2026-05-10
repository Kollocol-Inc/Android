package com.ziopam.kollocol.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.data.storage.datastore.UserDataStoreKeys
import com.ziopam.kollocol.domain.model.ThemeMode
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject

class PersonalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val safeApiCall: SafeApiCall,
    private val api: UserApi,
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) : PersonalRepository {

    private val avatarFile: File get() = context.filesDir.resolve("user_avatar")

    override suspend fun getUser(): AppResult<User> {
        val result = safeApiCall.call {
            api.getUser()
        }

        return when (result) {
            is AppResult.Ok -> {
                val user = result.value.toUser()
                updateUser(user)
                AppResult.Ok(user)
            }
            is AppResult.Err -> {
                val prefs = dataStore.data.first()
                val firstName = prefs[UserDataStoreKeys.FIRST_NAME]
                val lastName = prefs[UserDataStoreKeys.LAST_NAME]
                if (firstName != null && lastName != null) {
                    AppResult.Ok(User(
                        firstName = firstName,
                        lastName = lastName,
                        avatarUrl = prefs[UserDataStoreKeys.AVATAR_URL],
                        email = prefs[UserDataStoreKeys.EMAIL].orEmpty()
                    ))
                } else {
                    result
                }
            }
        }
    }

    override suspend fun updateUser(user: User) {
        dataStore.edit { prefs ->
            prefs[UserDataStoreKeys.FIRST_NAME] = user.firstName
            prefs[UserDataStoreKeys.LAST_NAME] = user.lastName
            prefs[UserDataStoreKeys.EMAIL] = user.email

            val avatarUrl = user.avatarUrl
            if (avatarUrl == null) {
                prefs.remove(UserDataStoreKeys.AVATAR_URL)
            } else {
                prefs[UserDataStoreKeys.AVATAR_URL] = avatarUrl
            }
        }
        val url = user.avatarUrl ?: return
        if (!avatarFile.exists()) {
            downloadAvatarFile(url)
        }
    }

    override suspend fun bumpAvatarVersion() {
        avatarFile.delete()
        dataStore.edit { prefs ->
            prefs[UserDataStoreKeys.AVATAR_VERSION] = System.currentTimeMillis()
        }
    }

    override suspend fun clear() {
        avatarFile.delete()
        dataStore.edit { prefs ->
            prefs.remove(UserDataStoreKeys.AVATAR_URL)
            prefs.remove(UserDataStoreKeys.AVATAR_VERSION)
            prefs.remove(UserDataStoreKeys.FIRST_NAME)
            prefs.remove(UserDataStoreKeys.LAST_NAME)
            prefs.remove(UserDataStoreKeys.EMAIL)
        }
    }

    override fun getUserFlow(): Flow<User> = dataStore.data.map { prefs ->
        val networkUrl = prefs[UserDataStoreKeys.AVATAR_URL]
        val local = avatarFile
        User(
            firstName = prefs[UserDataStoreKeys.FIRST_NAME].orEmpty(),
            lastName = prefs[UserDataStoreKeys.LAST_NAME].orEmpty(),
            avatarUrl = when {
                networkUrl == null -> null
                local.exists() -> Uri.fromFile(local).toString()
                else -> {
                    val version = prefs[UserDataStoreKeys.AVATAR_VERSION] ?: 0L
                    "$networkUrl?v=$version"
                }
            },
            email = prefs[UserDataStoreKeys.EMAIL].orEmpty()
        )
    }

    override fun getThemeMode(): Flow<ThemeMode> = dataStore.data.map { prefs ->
        when (prefs[UserDataStoreKeys.THEME_MODE]) {
            "dark" -> ThemeMode.DARK
            "system" -> ThemeMode.SYSTEM
            else -> ThemeMode.LIGHT
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[UserDataStoreKeys.THEME_MODE] = when (mode) {
                ThemeMode.DARK -> "dark"
                ThemeMode.LIGHT -> "light"
                ThemeMode.SYSTEM -> "system"
            }
        }
    }

    private suspend fun downloadAvatarFile(url: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes() ?: return@withContext
                    val temp = File(context.filesDir, "user_avatar_tmp")
                    temp.writeBytes(bytes)
                    temp.renameTo(avatarFile)
                }
            } catch (_: Exception) {
            }
        }
    }
}
