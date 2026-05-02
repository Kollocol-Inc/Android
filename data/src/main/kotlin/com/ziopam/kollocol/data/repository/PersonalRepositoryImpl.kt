package com.ziopam.kollocol.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.data.storage.datastore.UserDataStoreKeys
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PersonalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val safeApiCall: SafeApiCall,
    private val api: UserApi
) : PersonalRepository {

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
                        avatarUrl = prefs[UserDataStoreKeys.AVATAR_URL]
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

            val avatarUrl = user.avatarUrl
            if (avatarUrl == null) {
                prefs.remove(UserDataStoreKeys.AVATAR_URL)
            } else {
                prefs[UserDataStoreKeys.AVATAR_URL] = avatarUrl
            }
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(UserDataStoreKeys.AVATAR_URL)
            prefs.remove(UserDataStoreKeys.FIRST_NAME)
            prefs.remove(UserDataStoreKeys.LAST_NAME)
        }
    }
}
