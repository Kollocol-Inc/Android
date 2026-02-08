package com.ziopam.kollocol.core.session

import com.ziopam.kollocol.domain.repository.SessionRepository
import com.ziopam.kollocol.domain.repository.SessionState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeSessionRepository @Inject constructor() : SessionRepository {
    override val sessionState: Flow<SessionState>
        get() = TODO("Not yet implemented")

    override suspend fun hasValidSession(): Boolean = false
    override suspend fun saveTokens(access: String, refresh: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearSession() {
        TODO("Not yet implemented")
    }

    override suspend fun getAccessToken(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getRefreshToken(): String? {
        TODO("Not yet implemented")
    }
}
