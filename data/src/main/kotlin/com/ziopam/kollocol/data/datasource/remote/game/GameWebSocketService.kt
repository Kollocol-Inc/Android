package com.ziopam.kollocol.data.datasource.remote.game

import android.util.Log
import com.google.gson.Gson
import com.ziopam.kollocol.domain.model.AnswerOptionStats
import com.ziopam.kollocol.domain.model.AnswerResult
import com.ziopam.kollocol.domain.model.ConnectionState
import com.ziopam.kollocol.domain.model.GameEvent
import com.ziopam.kollocol.domain.model.GameFinishResult
import com.ziopam.kollocol.domain.model.GameParticipant
import com.ziopam.kollocol.domain.model.GameQuestion
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import com.ziopam.kollocol.domain.model.QuestionType
import com.ziopam.kollocol.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
    private val sessionRepository: SessionRepository
) {
    companion object {
        private const val TAG = "GameWebSocket"
        private const val BASE_WS_URL = "ws://130.193.58.223:8080/ws"
        private const val MAX_RECONNECT_ATTEMPTS = 8
        private const val INITIAL_RECONNECT_DELAY_MS = 1000L
        private const val NORMAL_CLOSURE = 1000
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.IDLE)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var webSocket: WebSocket? = null
    private var reconnectJob: Job? = null
    private var reconnectAttempts = 0
    private var currentAccessCode: String? = null
    // Incremented each time a new WebSocket is created so stale listeners ignore their callbacks
    private var generation = 0

    suspend fun connect(accessCode: String) {
        invalidateAndCloseSocket()
        currentAccessCode = accessCode
        reconnectAttempts = 0
        _connectionState.value = ConnectionState.CONNECTING
        doConnectInternal()
    }

    fun disconnect() {
        reconnectJob?.cancel()
        reconnectJob = null
        currentAccessCode = null
        invalidateAndCloseSocket()
        _connectionState.value = ConnectionState.IDLE
    }

    // Performs the actual WebSocket creation without resetting reconnectAttempts.
    // Used both by connect() and attemptReconnect().
    private suspend fun doConnectInternal() {
        val code = currentAccessCode ?: return

        val token = sessionRepository.getAccessToken()
        if (token.isNullOrBlank()) {
            _connectionState.value = ConnectionState.ERROR
            _events.emit(GameEvent.Error("No access token"))
            return
        }

        val gen = ++generation
        val url = "$BASE_WS_URL?token=$token&access_code=$code"
        val request = Request.Builder().url(url).build()
        webSocket = okHttpClient.newWebSocket(request, createListener(gen))
    }

    // Closes the current socket and increments the generation so the old listener is ignored.
    private fun invalidateAndCloseSocket() {
        generation++
        webSocket?.close(NORMAL_CLOSURE, null)
        webSocket = null
    }

    fun sendStartQuiz() {
        sendMessage(ClientMessageDto(type = "start_quiz"))
    }

    fun sendAnswer(questionId: String, answer: String, timeSpentMs: Long) {
        sendMessage(
            ClientMessageDto(
                type = "answer",
                payload = AnswerPayloadDto(
                    questionId = questionId,
                    answer = answer,
                    timeSpentMs = timeSpentMs
                )
            )
        )
    }

    fun sendContinue() {
        sendMessage(ClientMessageDto(type = "continue"))
    }

    fun sendKick(email: String) {
        sendMessage(ClientMessageDto(type = "kick", payload = KickPayloadDto(email = email)))
    }

    private fun sendMessage(message: ClientMessageDto) {
        val json = gson.toJson(message)
        Log.d(TAG, "Sending: $json")
        webSocket?.send(json)
    }

    private fun createListener(gen: Int) = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            if (gen != generation) return
            Log.d(TAG, "WebSocket opened")
            reconnectAttempts = 0
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (gen != generation) return
            Log.d(TAG, "Received: $text")
            val messages = text.split('\n')
            for (msgStr in messages) {
                if (msgStr.isBlank()) continue
                try {
                    val dto = gson.fromJson(msgStr, WebSocketMessageDto::class.java)
                    val event = mapToEvent(dto)
                    if (event != null) {
                        _events.tryEmit(event)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse message: $msgStr", e)
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (gen != generation) return
            val httpCode = response?.code
            Log.e(TAG, "WebSocket failure: ${t.message}, HTTP code: $httpCode", t)

            // 4xx on the very first connection attempt is a fatal error (wrong code, no auth, etc.)
            // During reconnection, 4xx can be temporary (e.g. server still holds the old session
            // and rejects the new one — server timeout is ~60 s), so we keep retrying.
            if (httpCode != null && httpCode in 400..499 && reconnectAttempts == 0) {
                val reason = when (httpCode) {
                    404 -> "Quiz not found"
                    401 -> "Unauthorized"
                    403 -> "Forbidden"
                    else -> "Error $httpCode"
                }
                _connectionState.value = ConnectionState.ERROR
                _events.tryEmit(GameEvent.Error(reason))
                currentAccessCode = null
                return
            }

            _connectionState.value = ConnectionState.DISCONNECTED
            _events.tryEmit(GameEvent.Disconnected(t.message))
            attemptReconnect()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            if (gen != generation) return
            Log.d(TAG, "WebSocket closing: $code $reason")
            webSocket.close(NORMAL_CLOSURE, null)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (gen != generation) return
            Log.d(TAG, "WebSocket closed: $code $reason")
            if (_connectionState.value != ConnectionState.IDLE) {
                _connectionState.value = ConnectionState.DISCONNECTED
                _events.tryEmit(GameEvent.Disconnected(reason))
            }
        }
    }

    private fun attemptReconnect() {
        if (currentAccessCode == null) return
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            _connectionState.value = ConnectionState.ERROR
            _events.tryEmit(GameEvent.Error("Failed to reconnect after $MAX_RECONNECT_ATTEMPTS attempts"))
            return
        }

        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val delayMs = minOf(INITIAL_RECONNECT_DELAY_MS shl reconnectAttempts, 12000L)
            reconnectAttempts++
            Log.d(TAG, "Reconnecting in ${delayMs}ms (attempt $reconnectAttempts)")
            delay(delayMs)
            invalidateAndCloseSocket()
            _connectionState.value = ConnectionState.CONNECTING
            doConnectInternal()
        }
    }

    private fun mapToEvent(dto: WebSocketMessageDto): GameEvent? {
        val payload = dto.payload
        return when (dto.type) {
            "connected" -> {
                val p = gson.fromJson(payload, ConnectedPayloadDto::class.java)
                _connectionState.value = ConnectionState.CONNECTED
                GameEvent.Connected(
                    sessionId = p.sessionId,
                    quizType = p.quizType,
                    quizStatus = p.quizStatus,
                    isCreator = p.isCreator
                )
            }

            "participants_update" -> {
                val p = gson.fromJson(payload, ParticipantsUpdatePayloadDto::class.java)
                GameEvent.ParticipantsUpdate(
                    action = p.action,
                    user = GameParticipant(
                        userId = p.user.userId,
                        name = "${p.user.firstName} ${p.user.lastName}".trim()
                            .ifBlank { p.user.userId },
                        email = p.user.email,
                        avatarUrl = p.user.avatarUrl,
                        isCreator = p.user.isCreator
                    ),
                    count = p.count
                )
            }

            "participants_list" -> {
                val p = gson.fromJson(payload, ParticipantsListPayloadDto::class.java)
                GameEvent.ParticipantsList(
                    participants = p.participants.map { participant ->
                        GameParticipant(
                            userId = participant.userId,
                            name = "${participant.firstName} ${participant.lastName}".trim()
                                .ifBlank { participant.userId },
                            email = participant.email,
                            avatarUrl = participant.avatarUrl,
                            isCreator = participant.isCreator
                        )
                    },
                    quizTitle = p.quiz?.title ?: ""
                )
            }

            "quiz_started" -> GameEvent.QuizStarted

            "question" -> {
                val p = gson.fromJson(payload, QuestionPayloadDto::class.java)
                GameEvent.QuestionReceived(
                    question = GameQuestion(
                        id = p.question.id,
                        text = p.question.text,
                        type = when (p.question.type) {
                            "single" -> QuestionType.SINGLE
                            "multiple" -> QuestionType.MULTIPLE
                            else -> QuestionType.OPEN
                        },
                        options = p.question.options,
                        questionIndex = p.questionIndex,
                        totalQuestions = p.totalQuestions,
                        timeLimitMs = if (p.timeLimitMs > 0) p.timeLimitMs
                                     else p.question.timeLimitSec * 1000L,
                        maxScore = p.question.maxScore,
                        serverTime = p.serverTime.toString()
                    )
                )
            }

            "answer_result" -> {
                val p = gson.fromJson(payload, AnswerResultPayloadDto::class.java)
                GameEvent.AnswerResultReceived(
                    result = AnswerResult(
                        isCorrect = p.isCorrect,
                        score = p.score,
                        timeSpentMs = p.timeSpentMs,
                        totalScore = p.totalScore
                    )
                )
            }

            "answer_progress" -> {
                val p = gson.fromJson(payload, AnswerProgressPayloadDto::class.java)
                GameEvent.AnswerProgress(
                    participantsAnswered = p.participantsAnswered,
                    totalParticipants = p.totalParticipants
                )
            }

            "leaderboard" -> {
                val p = gson.fromJson(payload, LeaderboardPayloadDto::class.java)
                GameEvent.LeaderboardUpdate(
                    leaderboard = p.leaderboard.map { entry ->
                        val user = entry.user
                        LeaderboardEntry(
                            rank = entry.rank,
                            userId = user.userId,
                            name = "${user.firstName} ${user.lastName}".trim()
                                .ifBlank { user.userId },
                            email = user.email,
                            avatarUrl = user.avatarUrl,
                            score = entry.score,
                            isAnswered = entry.isAnswered,
                            isOnline = user.isOnline
                        )
                    },
                    answerOptionStats = p.answerOptionStats?.map { s ->
                        AnswerOptionStats(option = s.option, count = s.count)
                    } ?: emptyList(),
                    canContinue = p.canContinue,
                    question = p.question?.let { q ->
                        GameQuestion(
                            id = q.question.id,
                            text = q.question.text,
                            type = when (q.question.type) {
                                "single" -> QuestionType.SINGLE
                                "multiple" -> QuestionType.MULTIPLE
                                else -> QuestionType.OPEN
                            },
                            options = q.question.options,
                            questionIndex = q.questionIndex,
                            totalQuestions = q.totalQuestions,
                            timeLimitMs = if (q.timeLimitMs > 0) q.timeLimitMs
                                         else q.question.timeLimitSec * 1000L,
                            maxScore = q.question.maxScore,
                            serverTime = q.serverTime.toString()
                        )
                    }
                )
            }

            "time_expired" -> {
                val p = gson.fromJson(payload, TimeExpiredPayloadDto::class.java)
                GameEvent.TimeExpired(questionIndex = p.questionIndex)
            }

            "waiting_for_creator" -> {
                val p = gson.fromJson(payload, WaitingForCreatorPayloadDto::class.java)
                GameEvent.WaitingForCreator(
                    questionIndex = p.questionIndex,
                    reason = p.reason
                )
            }

            "quiz_finished" -> {
                val p = gson.fromJson(payload, QuizFinishedPayloadDto::class.java)
                GameEvent.QuizFinished(
                    result = GameFinishResult(
                        finalScore = p.finalScore,
                        rank = p.rank
                    )
                )
            }

            "error" -> {
                val p = gson.fromJson(payload, ErrorPayloadDto::class.java)
                GameEvent.Error(message = p.message)
            }

            "pong" -> null

            else -> {
                Log.w(TAG, "Unknown message type: ${dto.type}")
                null
            }
        }
    }
}
