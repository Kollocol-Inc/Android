package com.ziopam.kollocol.domain.model

sealed interface GameEvent {
    data class Connected(
        val sessionId: String,
        val quizType: String,
        val quizStatus: String,
        val isCreator: Boolean
    ) : GameEvent

    data class ParticipantsUpdate(
        val action: String,
        val user: GameParticipant,
        val count: Int
    ) : GameEvent

    data class ParticipantsList(
        val participants: List<GameParticipant>,
        val quizTitle: String,
        val quizDeadline: String? = null
    ) : GameEvent

    data object QuizStarted : GameEvent

    data class QuestionReceived(
        val question: GameQuestion
    ) : GameEvent

    data class AnswerResultReceived(
        val result: AnswerResult
    ) : GameEvent

    data class AnswerProgress(
        val participantsAnswered: Int,
        val totalParticipants: Int
    ) : GameEvent

    data class LeaderboardUpdate(
        val leaderboard: List<LeaderboardEntry>,
        val answerOptionStats: List<AnswerOptionStats> = emptyList(),
        val canContinue: Boolean = false,
        val question: GameQuestion? = null
    ) : GameEvent

    data class TimeExpired(
        val questionIndex: Int
    ) : GameEvent

    data class WaitingForCreator(
        val questionIndex: Int,
        val reason: String?
    ) : GameEvent

    data class QuizFinished(
        val result: GameFinishResult
    ) : GameEvent

    data class Error(
        val message: String
    ) : GameEvent

    data class Disconnected(
        val reason: String? = null
    ) : GameEvent
}
