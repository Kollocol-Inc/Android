package com.ziopam.kollocol.core.session

interface SessionRepository {
    suspend fun hasValidSession(): Boolean
}