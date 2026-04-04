package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.data.datasource.remote.game.GameWebSocketService
import com.ziopam.kollocol.domain.model.ConnectionState
import com.ziopam.kollocol.domain.model.GameEvent
import com.ziopam.kollocol.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val webSocketService: GameWebSocketService
) : GameRepository {

    override val events: Flow<GameEvent> = webSocketService.events

    override val connectionState: StateFlow<ConnectionState> = webSocketService.connectionState

    override suspend fun connect(accessCode: String) {
        webSocketService.connect(accessCode)
    }

    override fun disconnect() {
        webSocketService.disconnect()
    }

    override fun sendStartQuiz() {
        webSocketService.sendStartQuiz()
    }

    override fun sendAnswer(questionId: String, answer: String, timeSpentMs: Long) {
        webSocketService.sendAnswer(questionId, answer, timeSpentMs)
    }

    override fun sendContinue() {
        webSocketService.sendContinue()
    }

    override fun sendKick(email: String) {
        webSocketService.sendKick(email)
    }
}
