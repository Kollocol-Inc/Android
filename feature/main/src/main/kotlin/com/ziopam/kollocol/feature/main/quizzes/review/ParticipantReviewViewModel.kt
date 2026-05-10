package com.ziopam.kollocol.feature.main.quizzes.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.model.ReviewAnswer
import com.ziopam.kollocol.domain.model.ReviewQuestion
import com.ziopam.kollocol.domain.repository.QuizRepository
import com.ziopam.kollocol.feature.main.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParticipantReviewState(
    val instanceTitle: String = "",
    val questions: List<ReviewQuestion> = emptyList(),
    val answers: List<ReviewAnswer> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val currentScore: Int = 0,
    val isLoading: Boolean = true,
    val isGrading: Boolean = false
) {
    val currentQuestion: ReviewQuestion? get() = questions.getOrNull(currentQuestionIndex)
    val currentAnswer: ReviewAnswer? get() = currentQuestion?.let { q -> answers.find { it.questionId == q.id } }
}

sealed class ParticipantReviewEvent {
    data class ShowError(val message: UiText) : ParticipantReviewEvent()
    data class ShowToast(val message: UiText) : ParticipantReviewEvent()
    object NavigateBack : ParticipantReviewEvent()
}

@HiltViewModel
class ParticipantReviewViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val instanceId: String = checkNotNull(savedStateHandle["instanceId"])
    private val participantId: String = checkNotNull(savedStateHandle["userId"])

    private val _state = MutableStateFlow(ParticipantReviewState())
    val state = _state.asStateFlow()

    private val _events = Channel<ParticipantReviewEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadAnswers()
    }

    private fun loadAnswers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = quizRepository.getParticipantAnswers(instanceId, participantId)) {
                is AppResult.Ok -> {
                    val data = result.value
                    val sorted = data.questions.sortedBy { it.orderIndex }
                    val firstAnswer = sorted.firstOrNull()?.let { q -> data.answers.find { it.questionId == q.id } }
                    _state.update {
                        it.copy(
                            instanceTitle = data.instanceTitle,
                            questions = sorted,
                            answers = data.answers,
                            currentQuestionIndex = 0,
                            currentScore = firstAnswer?.score ?: 0,
                            isLoading = false
                        )
                    }
                }
                is AppResult.Err -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ParticipantReviewEvent.ShowError(result.error.toUiText()))
                }
            }
        }
    }

    fun onSelectQuestion(index: Int) {
        val answer = _state.value.answers.find {
            it.questionId == _state.value.questions.getOrNull(index)?.id
        }
        _state.update { it.copy(currentQuestionIndex = index, currentScore = answer?.score ?: 0) }
    }

    fun onScoreIncrement() {
        val maxScore = _state.value.currentQuestion?.maxScore ?: 0
        _state.update { it.copy(currentScore = minOf(it.currentScore + 1, maxScore)) }
    }

    fun onScoreDecrement() {
        _state.update { it.copy(currentScore = maxOf(it.currentScore - 1, 0)) }
    }

    fun onGradeClick() {
        val question = _state.value.currentQuestion ?: return
        val score = _state.value.currentScore

        viewModelScope.launch {
            _state.update { it.copy(isGrading = true) }
            when (val result = quizRepository.gradeAnswer(instanceId, participantId, question.id, score)) {
                is AppResult.Ok -> {
                    val updatedAnswers = _state.value.answers.map { answer ->
                        if (answer.questionId == question.id) answer.copy(score = score, isReviewed = true) else answer
                    }
                    _state.update { it.copy(answers = updatedAnswers, isGrading = false) }
                    _events.send(ParticipantReviewEvent.ShowToast(UiText.StringRes(R.string.grade_saved)))
                }
                is AppResult.Err -> {
                    _state.update { it.copy(isGrading = false) }
                    _events.send(ParticipantReviewEvent.ShowError(result.error.toUiText()))
                }
            }
        }
    }
}
