package com.ziopam.kollocol.feature.main.quizzes.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.model.QuizParticipant
import com.ziopam.kollocol.domain.repository.QuizRepository
import com.ziopam.kollocol.feature.main.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class QuizReviewState(
    val instanceTitle: String = "",
    val instanceStatus: String = "",
    val deadline: String? = null,
    val participants: List<QuizParticipant> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val showCancelConfirm: Boolean = false,
    val isCanceling: Boolean = false,
    val isPublishing: Boolean = false
) {
    val activeParticipants: List<QuizParticipant>
        get() = participants.filter { it.sessionStatus == "in_progress" || it.sessionStatus == "finished" }
            .filter { p -> searchQuery.isBlank() || "${p.firstName} ${p.lastName}".contains(searchQuery, ignoreCase = true) || p.email.contains(searchQuery, ignoreCase = true) }

    val notStartedParticipants: List<QuizParticipant>
        get() = participants.filter { it.sessionStatus == "not_started" || it.sessionStatus == "joined" }
            .filter { p -> searchQuery.isBlank() || "${p.firstName} ${p.lastName}".contains(searchQuery, ignoreCase = true) || p.email.contains(searchQuery, ignoreCase = true) }

    private val finishedParticipants: List<QuizParticipant>
        get() = participants.filter { it.sessionStatus == "finished" }

    val reviewedCount: Int
        get() = finishedParticipants.count { it.reviewStatus == "reviewed" }

    val allReviewed: Boolean
        get() = finishedParticipants.isNotEmpty() && finishedParticipants.all { it.reviewStatus == "reviewed" }

    val isPublished: Boolean
        get() = instanceStatus == "published_results"
}

sealed class QuizReviewEvent {
    data class ShowError(val message: UiText) : QuizReviewEvent()
    object NavigateBack : QuizReviewEvent()
    data class ShowToast(val message: UiText) : QuizReviewEvent()
}

@HiltViewModel
class QuizReviewViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val instanceId: String = checkNotNull(savedStateHandle["instanceId"])

    private val _state = MutableStateFlow(QuizReviewState())
    val state = _state.asStateFlow()

    private val _events = Channel<QuizReviewEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadInstanceAndParticipants()
    }

    fun loadInstanceAndParticipants() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = quizRepository.getInstanceById(instanceId)) {
                is AppResult.Ok -> _state.update {
                    it.copy(
                        instanceTitle = result.value.title,
                        instanceStatus = result.value.status,
                        deadline = result.value.deadline
                    )
                }
                is AppResult.Err -> _events.send(QuizReviewEvent.ShowError(result.error.toUiText()))
            }
            when (val result = quizRepository.getInstanceParticipants(instanceId)) {
                is AppResult.Ok -> _state.update { it.copy(participants = result.value, isLoading = false) }
                is AppResult.Err -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(QuizReviewEvent.ShowError(result.error.toUiText()))
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onCancelQuizClick() {
        _state.update { it.copy(showCancelConfirm = true) }
    }

    fun onDismissCancelConfirm() {
        _state.update { it.copy(showCancelConfirm = false) }
    }

    fun onConfirmCancelQuiz() {
        viewModelScope.launch {
            _state.update { it.copy(showCancelConfirm = false, isCanceling = true) }
            when (val result = quizRepository.deleteInstance(instanceId)) {
                is AppResult.Ok -> {
                    _events.send(QuizReviewEvent.NavigateBack)
                }
                is AppResult.Err -> {
                    _state.update { it.copy(isCanceling = false) }
                    _events.send(QuizReviewEvent.ShowError(result.error.toUiText()))
                }
            }
        }
    }

    fun onPublishClick() {
        val current = _state.value
        if (current.isPublished) {
            viewModelScope.launch {
                _events.send(QuizReviewEvent.ShowToast(UiText.StringRes(R.string.results_already_published)))
            }
            return
        }
        val dl = current.deadline
        if (dl != null && isBeforeDeadline(dl)) {
            viewModelScope.launch {
                _events.send(QuizReviewEvent.ShowToast(UiText.StringRes(R.string.publish_before_deadline_error)))
            }
            return
        }
        if (!current.allReviewed) {
            viewModelScope.launch {
                _events.send(QuizReviewEvent.ShowToast(UiText.StringRes(R.string.publish_not_all_reviewed_error)))
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isPublishing = true) }
            when (val result = quizRepository.publishResults(instanceId)) {
                is AppResult.Ok -> {
                    _state.update { it.copy(isPublishing = false) }
                    _events.send(QuizReviewEvent.NavigateBack)
                }
                is AppResult.Err -> {
                    _state.update { it.copy(isPublishing = false) }
                    _events.send(QuizReviewEvent.ShowError(result.error.toUiText()))
                }
            }
        }
    }

    private fun isBeforeDeadline(deadline: String): Boolean {
        return try {
            Instant.parse(deadline).isAfter(Instant.now())
        } catch (e: Exception) {
            false
        }
    }

    fun getInstanceId(): String = instanceId
}
