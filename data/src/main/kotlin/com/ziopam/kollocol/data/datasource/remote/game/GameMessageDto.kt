package com.ziopam.kollocol.data.datasource.remote.game

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class WebSocketMessageDto(
    @SerializedName("type")
    val type: String,

    @SerializedName("payload")
    val payload: JsonObject? = null
)

data class ConnectedPayloadDto(
    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("quiz_type")
    val quizType: String,

    @SerializedName("quiz_status")
    val quizStatus: String,

    @SerializedName("is_creator")
    val isCreator: Boolean
)

data class ParticipantsUpdatePayloadDto(
    @SerializedName("action")
    val action: String,

    @SerializedName("user")
    val user: ParticipantDto,

    @SerializedName("count")
    val count: Int
)

data class ParticipantDto(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("first_name")
    val firstName: String = "",

    @SerializedName("last_name")
    val lastName: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("is_creator")
    val isCreator: Boolean = false,

    @SerializedName("is_online")
    val isOnline: Boolean = true
)

data class ParticipantsListQuizDto(
    @SerializedName("title")
    val title: String = "",

    @SerializedName("deadline")
    val deadline: String? = null
)

data class ParticipantsListPayloadDto(
    @SerializedName("participants")
    val participants: List<ParticipantDto> = emptyList(),

    @SerializedName("quiz")
    val quiz: ParticipantsListQuizDto? = null
)

data class KickPayloadDto(
    @SerializedName("email")
    val email: String
)

data class QuestionDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("options")
    val options: List<String>? = null,

    @SerializedName("order_index")
    val orderIndex: Int = 0,

    @SerializedName("max_score")
    val maxScore: Int = 0,

    @SerializedName("time_limit_sec")
    val timeLimitSec: Int = 0
)

data class QuestionPayloadDto(
    @SerializedName("question")
    val question: QuestionDto,

    @SerializedName("question_index")
    val questionIndex: Int,

    @SerializedName("total_questions")
    val totalQuestions: Int,

    @SerializedName("time_limit_ms")
    val timeLimitMs: Long = 0,

    @SerializedName("server_time")
    val serverTime: Long = 0
)

data class AnswerResultPayloadDto(
    @SerializedName("is_correct")
    val isCorrect: Boolean,

    @SerializedName("score")
    val score: Int,

    @SerializedName("time_spent_ms")
    val timeSpentMs: Long,

    @SerializedName("total_score")
    val totalScore: Int
)

data class LeaderboardEntryDto(
    @SerializedName("user")
    val user: ParticipantDto,

    @SerializedName("rank")
    val rank: Int,

    @SerializedName("score")
    val score: Int,

    @SerializedName("is_answered")
    val isAnswered: Boolean = false
)

data class AnswerOptionStatsDto(
    @SerializedName("option")
    val option: String,

    @SerializedName("count")
    val count: Int
)

data class LeaderboardPayloadDto(
    @SerializedName("leaderboard")
    val leaderboard: List<LeaderboardEntryDto>,

    @SerializedName("questions_stats")
    val answerOptionStats: List<AnswerOptionStatsDto>? = null,

    @SerializedName("can_continue")
    val canContinue: Boolean = false,

    @SerializedName("question")
    val question: QuestionPayloadDto? = null
)

data class TimeExpiredPayloadDto(
    @SerializedName("question_index")
    val questionIndex: Int
)

data class WaitingForCreatorPayloadDto(
    @SerializedName("question_index")
    val questionIndex: Int,

    @SerializedName("reason")
    val reason: String? = null
)

data class AnswerProgressPayloadDto(
    @SerializedName("participants_answered")
    val participantsAnswered: Int,

    @SerializedName("total_participants")
    val totalParticipants: Int
)

data class QuizFinishedPayloadDto(
    @SerializedName("final_score")
    val finalScore: Int,

    @SerializedName("rank")
    val rank: Int
)

data class ErrorPayloadDto(
    @SerializedName("message")
    val message: String
)

data class ClientMessageDto(
    @SerializedName("type")
    val type: String,

    @SerializedName("payload")
    val payload: Any? = null
)

data class AnswerPayloadDto(
    @SerializedName("question_id")
    val questionId: String,

    @SerializedName("answer")
    val answer: String,

    @SerializedName("time_spent_ms")
    val timeSpentMs: Long
)
