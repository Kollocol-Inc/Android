package com.ziopam.kollocol.domain.model

data class GameParticipant(
    val userId: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val isCreator: Boolean = false
)

data class GameQuestion(
    val id: String,
    val text: String,
    val type: QuestionType,
    val options: List<String>? = null,
    val questionIndex: Int,
    val totalQuestions: Int,
    val timeLimitMs: Long,
    val maxScore: Int = 0,
    val serverTime: String? = null
)

enum class QuestionType {
    SINGLE,
    MULTIPLE,
    OPEN
}

data class AnswerResult(
    val isCorrect: Boolean,
    val score: Int,
    val timeSpentMs: Long,
    val totalScore: Int
)

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val score: Int,
    val isAnswered: Boolean = false,
    val isOnline: Boolean = true
)

data class AnswerOptionStats(
    val option: String,
    val count: Int
)

data class AnswerProgress(
    val participantsAnswered: Int,
    val totalParticipants: Int
)

data class GameFinishResult(
    val finalScore: Int,
    val rank: Int
)

enum class ConnectionState {
    IDLE,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    ERROR
}
