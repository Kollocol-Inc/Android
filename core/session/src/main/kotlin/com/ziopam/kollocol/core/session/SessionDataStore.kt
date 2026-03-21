package com.ziopam.kollocol.core.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ziopam.kollocol.core.session.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val _keyAccess = stringPreferencesKey("access_token")
    private val _keyRefresh = stringPreferencesKey("refresh_token")

    val tokensFlow: Flow<SessionTokens> = context.dataStore.data.map { prefs ->
        SessionTokens(
            accessToken = prefs[_keyAccess],
            refreshToken = prefs[_keyRefresh]
        )
    }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[_keyAccess] = access
            prefs[_keyRefresh] = refresh
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(_keyAccess)
            prefs.remove(_keyRefresh)
        }
    }
}