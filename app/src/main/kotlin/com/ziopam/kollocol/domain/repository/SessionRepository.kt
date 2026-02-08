package com.ziopam.kollocol.domain.repository

import kotlinx.coroutines.flow.Flow

sealed interface SessionState {
    data object Unauthorized : SessionState
    data object Authorized: SessionState
}

interface SessionRepository {
    val sessionState: Flow<SessionState>

    suspend fun hasValidSession(): Boolean
    suspend fun saveTokens(access: String, refresh: String)
    suspend fun clearSession()

    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
}