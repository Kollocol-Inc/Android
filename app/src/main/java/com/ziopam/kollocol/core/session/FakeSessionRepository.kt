package com.ziopam.kollocol.core.session

import javax.inject.Inject

class FakeSessionRepository @Inject constructor() : SessionRepository {
    override suspend fun hasValidSession(): Boolean = false
}
