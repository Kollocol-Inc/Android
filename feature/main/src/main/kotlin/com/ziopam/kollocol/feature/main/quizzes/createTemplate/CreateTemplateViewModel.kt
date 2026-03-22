package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class QuestionType { SINGLE, MULTIPLE, OPEN }

data class QuestionUiModel(
    val text: String = "",
    val type: QuestionType = QuestionType.SINGLE,
    val options: List<String> = listOf("", "", "", ""),
    val correctAnswer: String = "",
    val correctOptionIndices: Set<Int> = emptySet(),
    val maxScore: Int = 10,
    val timeLimitSec: Int = 30,
)

data class CreateTemplateUiState(
    val title: String = "",
    val quizType: String = "async",
    val randomOrder: Boolean = false,
    val questions: List<QuestionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val showAddQuestionSheet: Boolean = false,
    val editingQuestionIndex: Int? = null,
    val errorMessage: String? = null,
)

sealed class CreateTemplateEvent {
    data object NavigateBack : CreateTemplateEvent()
    data class ShowError(val message: String) : CreateTemplateEvent()
}

@HiltViewModel
class CreateTemplateViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTemplateUiState())
    val uiState: StateFlow<CreateTemplateUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateTemplateEvent>()
    val events = _events.asSharedFlow()

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onQuizTypeToggle() {
        _uiState.update {
            it.copy(quizType = if (it.quizType == "async") "sync" else "async")
        }
    }

    fun onRandomOrderToggle() {
        _uiState.update { it.copy(randomOrder = !it.randomOrder) }
    }

    fun showAddQuestionSheet(editIndex: Int? = null) {
        _uiState.update {
            it.copy(showAddQuestionSheet = true, editingQuestionIndex = editIndex)
        }
    }

    fun hideAddQuestionSheet() {
        _uiState.update {
            it.copy(showAddQuestionSheet = false, editingQuestionIndex = null)
        }
    }

    fun addQuestion(question: QuestionUiModel) {
        _uiState.update {
            it.copy(
                questions = it.questions + question,
                showAddQuestionSheet = false,
                editingQuestionIndex = null
            )
        }
    }

    fun editQuestion(index: Int, question: QuestionUiModel) {
        _uiState.update {
            val updated = it.questions.toMutableList()
            updated[index] = question
            it.copy(
                questions = updated,
                showAddQuestionSheet = false,
                editingQuestionIndex = null
            )
        }
    }

    fun deleteQuestion(index: Int) {
        _uiState.update {
            it.copy(questions = it.questions.toMutableList().apply { removeAt(index) })
        }
    }

    fun saveTemplate() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "fill_title") }
            return
        }
        if (state.questions.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "add_question") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val questionMaps = state.questions.map { q ->
                val correctAnswer: Any = when (q.type) {
                    QuestionType.SINGLE -> q.correctOptionIndices.firstOrNull() ?: 0
                    QuestionType.MULTIPLE -> q.correctOptionIndices.sorted()
                    QuestionType.OPEN -> q.correctAnswer
                }

                mapOf (
                    "text" to q.text,
                    "type" to when (q.type) {
                        QuestionType.SINGLE -> "single"
                        QuestionType.MULTIPLE -> "multiple"
                        QuestionType.OPEN -> "open"
                    },
                    "correct_answer" to correctAnswer,
                    "max_score" to q.maxScore,
                    "options" to if (q.type != QuestionType.OPEN) q.options.filter { it.isNotBlank() } else null,
                    "time_limit_sec" to q.timeLimitSec
                )
            }

            val result = quizRepository.createTemplate(
                title = state.title,
                quizType = state.quizType,
                questions = questionMaps,
                randomOrder = state.randomOrder
            )

            when (result) {
                is AppResult.Ok -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CreateTemplateEvent.NavigateBack)
                }
                is AppResult.Err -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.error.toString())
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
