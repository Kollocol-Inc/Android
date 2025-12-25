package com.ziopam.kollocol.feature.session

interface SessionRepository {
    suspend fun hasValidSession(): Boolean
}