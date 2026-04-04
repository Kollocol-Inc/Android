package com.ziopam.kollocol.feature.quizgame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.domain.model.AnswerProgress
import com.ziopam.kollocol.domain.model.GameEvent
import com.ziopam.kollocol.domain.model.GameParticipant
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import com.ziopam.kollocol.domain.model.QuestionType
import com.ziopam.kollocol.domain.repository.GameRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val personalRepository: PersonalRepository
) : ViewModel() {

    private val accessCode: String = savedStateHandle["accessCode"] ?: ""

    private val _uiState = MutableStateFlow(GameUiState(accessCode = accessCode))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameUiEvent>()
    val events = _events.asSharedFlow()

    private var timerJob: Job? = null
    private var questionStartTimeMs: Long = 0L
    private var isCreator: Boolean = false
    private var lastLeaderboard: List<LeaderboardEntry> = emptyList()
    private var knownParticipants: List<GameParticipant> = emptyList()
    private var midQuestionLeaderboardReceived = false
    private var selfName: String = ""

    init {
        observeEvents()
        connect()
        fetchSelfName()
    }

    private fun fetchSelfName() {
        viewModelScope.launch {
            val result = personalRepository.getUser()
            if (result is AppResult.Ok) {
                val user = result.value
                selfName = "${user.firstName} ${user.lastName}".trim()
                updateSelfUserId()
            }
        }
    }

    private fun updateSelfUserId() {
        val phase = _uiState.value.phase as? GamePhase.Lobby ?: return
        val selfId = findSelfId(phase.participants) ?: return
        _uiState.update { it.copy(selfUserId = selfId) }
    }

    private fun findSelfId(participants: List<GameParticipant>): String? {
        return if (isCreator) {
            participants.firstOrNull { it.isCreator }?.userId
        } else {
            participants.firstOrNull { it.name == selfName }?.userId
        }
    }

    private fun connect() {
        viewModelScope.launch {
            gameRepository.connect(accessCode)
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            gameRepository.events.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: GameEvent) {
        when (event) {
            is GameEvent.Connected -> handleConnected(event)
            is GameEvent.ParticipantsUpdate -> handleParticipantsUpdate(event)
            is GameEvent.ParticipantsList -> handleParticipantsList(event)
            is GameEvent.QuizStarted -> handleQuizStarted()
            is GameEvent.QuestionReceived -> handleQuestion(event)
            is GameEvent.AnswerResultReceived -> handleAnswerResult(event)
            is GameEvent.AnswerProgress -> handleAnswerProgress(event)
            is GameEvent.LeaderboardUpdate -> handleLeaderboard(event)
            is GameEvent.TimeExpired -> handleTimeExpired()
            is GameEvent.WaitingForCreator -> handleWaitingForCreator()
            is GameEvent.QuizFinished -> handleQuizFinished(event)
            is GameEvent.Error -> handleError(event)
            is GameEvent.Disconnected -> {
                val phase = _uiState.value.phase
                if (phase !is GamePhase.Connecting && phase !is GamePhase.Error) {
                    _uiState.update { it.copy(isReconnecting = true) }
                }
            }
        }
    }

    private fun handleConnected(event: GameEvent.Connected) {
        isCreator = event.isCreator

        when (event.quizStatus) {
            "waiting" -> {
                _uiState.update {
                    it.copy(isReconnecting = false, phase = GamePhase.Lobby(isCreator = event.isCreator))
                }
            }
            "finished" -> {
                _uiState.update {
                    it.copy(isReconnecting = false, phase = GamePhase.Finished())
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        isReconnecting = false,
                        phase = GamePhase.Playing(
                            isCreator = event.isCreator,
                            showStats = event.isCreator,
                            leaderboard = lastLeaderboard
                        )
                    )
                }
            }
        }
    }

    private fun handleParticipantsUpdate(event: GameEvent.ParticipantsUpdate) {
        _uiState.update { state ->
            when (val phase = state.phase) {
                is GamePhase.Lobby -> {
                    val participants = phase.participants.toMutableList()
                    when (event.action) {
                        "joined" -> {
                            if (participants.none { it.userId == event.user.userId }) {
                                participants.add(event.user)
                            }
                        }
                        "left" -> participants.removeAll { it.userId == event.user.userId }
                    }
                    knownParticipants = participants.filter { !it.isCreator }
                    val nonCreatorCount = knownParticipants.size
                    val selfId = findSelfId(participants)
                    state.copy(
                        totalParticipants = nonCreatorCount,
                        selfUserId = selfId ?: state.selfUserId,
                        phase = phase.copy(
                            participants = participants,
                            participantCount = participants.size
                        )
                    )
                }
                is GamePhase.Playing -> {
                    val isOnline = event.action == "joined"
                    val updatedLeaderboard = phase.leaderboard.map { entry ->
                        if (entry.userId == event.user.userId) entry.copy(isOnline = isOnline)
                        else entry
                    }
                    lastLeaderboard = updatedLeaderboard
                    state.copy(phase = phase.copy(leaderboard = updatedLeaderboard))
                }
                else -> state
            }
        }
    }

    private fun handleParticipantsList(event: GameEvent.ParticipantsList) {
        knownParticipants = event.participants.filter { !it.isCreator }
        val nonCreatorCount = knownParticipants.size
        _uiState.update { state ->
            val quizName = event.quizTitle.ifBlank { state.quizName }
            when (val phase = state.phase) {
                is GamePhase.Lobby -> {
                    val selfId = findSelfId(event.participants)
                    state.copy(
                        quizName = quizName,
                        totalParticipants = nonCreatorCount,
                        selfUserId = selfId ?: state.selfUserId,
                        phase = phase.copy(
                            participants = event.participants,
                            participantCount = event.participants.size
                        )
                    )
                }
                else -> state.copy(quizName = quizName, totalParticipants = nonCreatorCount)
            }
        }
    }

    private fun handleQuizStarted() {
        _uiState.update {
            it.copy(
                phase = GamePhase.Playing(
                    isCreator = isCreator,
                    showStats = isCreator
                )
            )
        }
    }

    private fun handleQuestion(event: GameEvent.QuestionReceived) {
        val question = event.question
        val preserveAnswered = midQuestionLeaderboardReceived
        midQuestionLeaderboardReceived = false
        questionStartTimeMs = System.currentTimeMillis()

        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing
            val leaderboard = (playing?.leaderboard?.takeIf { it.isNotEmpty() }
                ?: lastLeaderboard.takeIf { it.isNotEmpty() }
                ?: knownParticipants.mapIndexed { idx, p ->
                    LeaderboardEntry(
                        rank = idx + 1,
                        userId = p.userId,
                        name = p.name,
                        email = p.email,
                        avatarUrl = p.avatarUrl,
                        score = 0
                    )
                })
                .let { entries -> if (preserveAnswered) entries else entries.map { it.copy(isAnswered = false) } }
            state.copy(
                phase = GamePhase.Playing(
                    question = question,
                    isCreator = isCreator,
                    totalScore = playing?.totalScore ?: 0,
                    leaderboard = leaderboard,
                    showStats = isCreator,
                    timeRemainingMs = question.timeLimitMs
                )
            )
        }

        startTimer(question.timeLimitMs)
    }

    private fun handleAnswerResult(event: GameEvent.AnswerResultReceived) {
        val result = event.result
        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing ?: return@update state
            state.copy(
                phase = playing.copy(
                    answerResult = result,
                    hasAnswered = true,
                    totalScore = result.totalScore
                )
            )
        }

        val toastMsg = if (result.isCorrect) {
            UiText.StringRes(R.string.game_answer_correct, listOf(result.score))
        } else {
            UiText.StringRes(R.string.game_answer_incorrect)
        }
        viewModelScope.launch { _events.emit(GameUiEvent.ShowToast(toastMsg)) }
    }

    private fun handleAnswerProgress(event: GameEvent.AnswerProgress) {
        val allAnswered = event.totalParticipants > 0 &&
                event.participantsAnswered >= event.totalParticipants
        if (allAnswered) timerJob?.cancel()
        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing ?: return@update state
            state.copy(
                totalParticipants = event.totalParticipants,
                phase = playing.copy(
                    answerProgress = AnswerProgress(
                        participantsAnswered = event.participantsAnswered,
                        totalParticipants = event.totalParticipants
                    ),
                    timeRemainingMs = if (allAnswered) 0L else playing.timeRemainingMs
                )
            )
        }
    }

    private fun handleLeaderboard(event: GameEvent.LeaderboardUpdate) {
        val isMidQuestion = event.question != null
        val allAnswered = event.leaderboard.isNotEmpty() && event.leaderboard.all { it.isAnswered }
        if (!isMidQuestion || allAnswered) timerJob?.cancel()
        if (isMidQuestion && !allAnswered) midQuestionLeaderboardReceived = true

        lastLeaderboard = event.leaderboard
        _uiState.update { state ->
            when (val phase = state.phase) {
                is GamePhase.Playing -> {
                    val answeredCount = event.leaderboard.count { it.isAnswered }
                    val total = state.totalParticipants.takeIf { it > 0 }
                        ?: event.leaderboard.size.takeIf { it > 0 }
                        ?: answeredCount
                    val updatedProgress = AnswerProgress(
                        participantsAnswered = answeredCount,
                        totalParticipants = total
                    )
                    state.copy(
                        phase = phase.copy(
                            question = phase.question ?: event.question,
                            leaderboard = event.leaderboard,
                            answerOptionStats = event.answerOptionStats,
                            showStats = true,
                            canContinue = phase.canContinue || event.canContinue,
                            timeRemainingMs = if (isMidQuestion) phase.timeRemainingMs else 0L,
                            answerProgress = updatedProgress
                        )
                    )
                }
                is GamePhase.Finished -> state.copy(
                    phase = phase.copy(leaderboard = event.leaderboard)
                )
                else -> state
            }
        }
    }

    private fun handleTimeExpired() {
        timerJob?.cancel()
        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing ?: return@update state
            state.copy(
                phase = playing.copy(
                    timeExpired = true,
                    timeRemainingMs = 0,
                    showStats = true,
                    canContinue = true
                )
            )
        }
        viewModelScope.launch {
            _events.emit(GameUiEvent.ShowToast(UiText.StringRes(R.string.game_time_expired)))
        }
    }

    private fun handleWaitingForCreator() {
        timerJob?.cancel()
        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing ?: return@update state
            state.copy(
                phase = playing.copy(
                    waitingForCreator = true,
                    showStats = true,
                    timeRemainingMs = 0L,
                    canContinue = true
                )
            )
        }
    }

    private fun handleQuizFinished(event: GameEvent.QuizFinished) {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                phase = GamePhase.Finished(
                    result = event.result,
                    leaderboard = lastLeaderboard
                )
            )
        }
    }

    private fun handleError(event: GameEvent.Error) {
        val currentPhase = _uiState.value.phase

        val dismissMessage: UiText? = when (event.message) {
            "Session replaced by another device" -> UiText.StringRes(R.string.game_error_session_replaced)
            "You have been kicked" -> UiText.StringRes(R.string.game_error_kicked)
            else -> null
        }

        if (dismissMessage != null) {
            if (currentPhase is GamePhase.Connecting) {
                _uiState.update { it.copy(phase = GamePhase.Error(message = dismissMessage)) }
            } else {
                _uiState.update { it.copy(isReconnecting = false) }
                gameRepository.disconnect()
                viewModelScope.launch {
                    _events.emit(GameUiEvent.ShowToast(dismissMessage))
                    _events.emit(GameUiEvent.NavigateBack)
                }
            }
            return
        }

        if (currentPhase is GamePhase.Connecting) {
            val message = when (event.message) {
                "Quiz has already finished" -> UiText.StringRes(R.string.game_error_quiz_already_finished)
                "Quiz has already started" -> UiText.StringRes(R.string.game_error_quiz_already_started)
                else -> UiText.Dynamic(event.message)
            }
            _uiState.update { it.copy(phase = GamePhase.Error(message = message)) }
        } else {
            _uiState.update { it.copy(isReconnecting = false) }
            viewModelScope.launch {
                _events.emit(GameUiEvent.ShowToast(UiText.Dynamic(event.message)))
            }
        }
    }

    private fun startTimer(timeLimitMs: Long) {
        timerJob?.cancel()
        if (timeLimitMs <= 0) return

        timerJob = viewModelScope.launch {
            var remaining = timeLimitMs
            while (remaining > 0) {
                _uiState.update { state ->
                    val playing = state.phase as? GamePhase.Playing ?: return@update state
                    state.copy(phase = playing.copy(timeRemainingMs = remaining))
                }
                delay(100)
                remaining -= 100
            }
            _uiState.update { state ->
                val playing = state.phase as? GamePhase.Playing ?: return@update state
                state.copy(phase = playing.copy(timeRemainingMs = 0))
            }
        }
    }

    fun startQuiz() {
        gameRepository.sendStartQuiz()
    }

    fun selectAnswer(index: Int) {
        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing ?: return@update state
            if (playing.hasAnswered || playing.timeExpired) return@update state

            val question = playing.question ?: return@update state
            val newSelection = when (question.type) {
                QuestionType.SINGLE -> setOf(index)
                QuestionType.MULTIPLE -> {
                    if (index in playing.selectedAnswers) {
                        playing.selectedAnswers - index
                    } else {
                        playing.selectedAnswers + index
                    }
                }
                QuestionType.OPEN -> playing.selectedAnswers
            }

            state.copy(phase = playing.copy(selectedAnswers = newSelection))
        }
    }

    fun setOpenAnswer(text: String) {
        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing ?: return@update state
            state.copy(phase = playing.copy(openAnswer = text))
        }
    }

    fun submitAnswer() {
        val state = _uiState.value
        val playing = state.phase as? GamePhase.Playing ?: return
        val question = playing.question ?: return
        if (playing.hasAnswered || playing.timeExpired) return

        val timeSpentMs = System.currentTimeMillis() - questionStartTimeMs

        val answer = when (question.type) {
            QuestionType.SINGLE -> {
                playing.selectedAnswers.firstOrNull()?.toString() ?: return
            }
            QuestionType.MULTIPLE -> {
                val indices = playing.selectedAnswers.sorted()
                indices.joinToString(prefix = "[", postfix = "]")
            }
            QuestionType.OPEN -> {
                playing.openAnswer.ifBlank { return }
            }
        }

        gameRepository.sendAnswer(question.id, answer, timeSpentMs)

        _uiState.update {
            it.copy(phase = playing.copy(hasAnswered = true))
        }
    }

    fun nextQuestion() {
        gameRepository.sendContinue()
        _uiState.update { state ->
            val playing = state.phase as? GamePhase.Playing ?: return@update state
            val reset = playing.leaderboard.map { it.copy(isAnswered = false) }
            lastLeaderboard = reset
            state.copy(phase = playing.copy(leaderboard = reset, canContinue = false))
        }
    }

    fun kickParticipant(email: String) {
        gameRepository.sendKick(email)
    }

    fun retryConnect() {
        _uiState.update { it.copy(phase = GamePhase.Connecting, isReconnecting = false) }
        connect()
    }

    fun navigateBack() {
        viewModelScope.launch {
            _events.emit(GameUiEvent.NavigateBack)
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameRepository.disconnect()
    }
}
