package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.domain.model.ConnectionState
import com.ziopam.kollocol.domain.model.GameEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface GameRepository {
    val events: Flow<GameEvent>
    val connectionState: StateFlow<ConnectionState>

    suspend fun connect(accessCode: String)
    fun disconnect()
    fun sendStartQuiz()
    fun sendAnswer(questionId: String, answer: String, timeSpentMs: Long)
    fun sendContinue()
    fun sendKick(email: String)
}
