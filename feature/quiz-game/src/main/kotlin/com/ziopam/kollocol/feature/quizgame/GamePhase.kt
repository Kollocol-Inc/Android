package com.ziopam.kollocol.feature.quizgame

import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.domain.model.AnswerOptionStats
import com.ziopam.kollocol.domain.model.AnswerProgress
import com.ziopam.kollocol.domain.model.AnswerResult
import com.ziopam.kollocol.domain.model.GameFinishResult
import com.ziopam.kollocol.domain.model.GameParticipant
import com.ziopam.kollocol.domain.model.GameQuestion
import com.ziopam.kollocol.domain.model.LeaderboardEntry

sealed interface GamePhase {
    data object Connecting : GamePhase

    data object AsyncInfo : GamePhase

    data class Lobby(
        val participants: List<GameParticipant> = emptyList(),
        val participantCount: Int = 0,
        val isCreator: Boolean = false
    ) : GamePhase

    data class Playing(
        val question: GameQuestion? = null,
        val selectedAnswers: Set<Int> = emptySet(),
        val openAnswer: String = "",
        val hasAnswered: Boolean = false,
        val answerResult: AnswerResult? = null,
        val leaderboard: List<LeaderboardEntry> = emptyList(),
        val showStats: Boolean = false,
        val isCreator: Boolean = false,
        val totalScore: Int = 0,
        val timeRemainingMs: Long = 0,
        val timeExpired: Boolean = false,
        val waitingForCreator: Boolean = false,
        val answerOptionStats: List<AnswerOptionStats> = emptyList(),
        val answerProgress: AnswerProgress? = null,
        val canContinue: Boolean = false
    ) : GamePhase

    data class Finished(
        val result: GameFinishResult? = null,
        val leaderboard: List<LeaderboardEntry> = emptyList()
    ) : GamePhase

    data class Error(
        val message: UiText
    ) : GamePhase
}

data class GameUiState(
    val phase: GamePhase = GamePhase.Connecting,
    val quizName: String = "",
    val accessCode: String = "",
    val isReconnecting: Boolean = false,
    val totalParticipants: Int = 0,
    val selfUserId: String = "",
    val isAsync: Boolean = false,
    val quizDeadline: String? = null
)

sealed interface GameUiEvent {
    data class ShowToast(val message: UiText) : GameUiEvent
    data object NavigateBack : GameUiEvent
}
