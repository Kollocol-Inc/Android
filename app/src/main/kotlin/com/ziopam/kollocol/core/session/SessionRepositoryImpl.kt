package com.ziopam.kollocol.core.session

import com.ziopam.kollocol.domain.repository.SessionRepository
import com.ziopam.kollocol.domain.repository.SessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val store: SessionDataStore
) : SessionRepository {

    override val sessionState: Flow<SessionState> =
        store.tokensFlow.map { tokens ->
            val ok = !tokens.accessToken.isNullOrBlank() && !tokens.refreshToken.isNullOrBlank()
            if (ok) SessionState.Authorized else SessionState.Unauthorized
        }

    override suspend fun hasValidSession(): Boolean {
        val tokens = store.tokensFlow.first()
        return !tokens.accessToken.isNullOrBlank() && !tokens.refreshToken.isNullOrBlank()
    }

    override suspend fun saveTokens(access: String, refresh: String) = store.saveTokens(access, refresh)
    override suspend fun clearSession() = store.clear()

    override suspend fun getAccessToken(): String? = store.tokensFlow.first().accessToken
    override suspend fun getRefreshToken(): String? = store.tokensFlow.first().refreshToken
}
