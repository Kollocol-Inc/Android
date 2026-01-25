package com.ziopam.kollocol.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.ziopam.kollocol.data.storage.datastore.UserDataStoreKeys
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PersonalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PersonalRepository {

    override val user: Flow<User> =
        dataStore.data.map { prefs ->
            User(
                avatarUrl = prefs[UserDataStoreKeys.AVATAR_URL],
                firstName = prefs[UserDataStoreKeys.FIRST_NAME].orEmpty(),
                lastName = prefs[UserDataStoreKeys.LAST_NAME].orEmpty()
            )
        }

    override suspend fun updateUser(user: User) {
        dataStore.edit { prefs ->
            prefs[UserDataStoreKeys.FIRST_NAME] = user.firstName
            prefs[UserDataStoreKeys.LAST_NAME] = user.lastName

            if (user.avatarUrl == null) {
                prefs.remove(UserDataStoreKeys.AVATAR_URL)
            } else {
                prefs[UserDataStoreKeys.AVATAR_URL] = user.avatarUrl
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
