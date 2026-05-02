package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.model.TemplateQuestion
import com.ziopam.kollocol.domain.repository.QuizRepository
import com.ziopam.kollocol.feature.main.R
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
    val isLoadingTemplate: Boolean = false,
    val isGeneratingQuestions: Boolean = false,
    val showAddQuestionSheet: Boolean = false,
    val editingQuestionIndex: Int? = null,
    val errorMessage: UiText? = null,
    val showDeleteConfirmDialog: Boolean = false,
    val showUnsavedChangesDialog: Boolean = false,
    val showGenerateQuestionsSheet: Boolean = false,
    val generateQuestionsPrompt: String = "",
    val hasUnsavedChanges: Boolean = false
)

sealed class CreateTemplateEvent {
    data object NavigateBack : CreateTemplateEvent()
    data class ShowToast(val message: UiText) : CreateTemplateEvent()
}

private data class TemplateSnapshot(
    val title: String,
    val quizType: String,
    val randomOrder: Boolean,
    val questions: List<QuestionUiModel>
)

@HiltViewModel
class CreateTemplateViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val templateId: String? = savedStateHandle["templateId"]
    private val aiPromptArg: String? = savedStateHandle["aiPrompt"]

    val isEditMode: Boolean = templateId != null

    private var _initialSnapshot = TemplateSnapshot("", "async", false, emptyList())

    private val _uiState = MutableStateFlow(CreateTemplateUiState())
    val uiState: StateFlow<CreateTemplateUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateTemplateEvent>()
    val events = _events.asSharedFlow()

    init {
        if (isEditMode) {
            loadTemplate(templateId!!)
        } else {
            _initialSnapshot = TemplateSnapshot("", "async", false, emptyList())
            if (!aiPromptArg.isNullOrBlank()) {
                generateFromAi(aiPromptArg)
            }
        }
    }

    private fun loadTemplate(id: String) {
        _uiState.update { it.copy(isLoadingTemplate = true) }
        viewModelScope.launch {
            val result = quizRepository.getTemplateById(id)
            when (result) {
                is AppResult.Ok -> {
                    val detail = result.value
                    val questions = detail.questions.map { it.toUiModel() }
                    _initialSnapshot = TemplateSnapshot(
                        title = detail.title,
                        quizType = detail.quizType,
                        randomOrder = detail.randomOrder,
                        questions = questions
                    )
                    _uiState.update {
                        it.copy(
                            title = detail.title,
                            quizType = detail.quizType,
                            randomOrder = detail.randomOrder,
                            questions = questions,
                            isLoadingTemplate = false,
                            hasUnsavedChanges = false
                        )
                    }
                }
                is AppResult.Err -> {
                    _uiState.update { it.copy(isLoadingTemplate = false) }
                    _events.emit(CreateTemplateEvent.ShowToast(result.error.toUiText()))
                }
            }
        }
    }

    private fun computeHasChanges(): Boolean {
        val state = _uiState.value
        return state.title != _initialSnapshot.title ||
                state.quizType != _initialSnapshot.quizType ||
                state.randomOrder != _initialSnapshot.randomOrder ||
                state.questions != _initialSnapshot.questions
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, hasUnsavedChanges = computeHasChangesWith(title = title)) }
    }

    private fun computeHasChangesWith(
        title: String = _uiState.value.title,
        quizType: String = _uiState.value.quizType,
        randomOrder: Boolean = _uiState.value.randomOrder,
        questions: List<QuestionUiModel> = _uiState.value.questions
    ): Boolean {
        return title != _initialSnapshot.title ||
                quizType != _initialSnapshot.quizType ||
                randomOrder != _initialSnapshot.randomOrder ||
                questions != _initialSnapshot.questions
    }

    fun onQuizTypeToggle() {
        val newType = if (_uiState.value.quizType == "async") "sync" else "async"
        _uiState.update { it.copy(quizType = newType, hasUnsavedChanges = computeHasChangesWith(quizType = newType)) }
    }

    fun onRandomOrderToggle() {
        val newVal = !_uiState.value.randomOrder
        _uiState.update { it.copy(randomOrder = newVal, hasUnsavedChanges = computeHasChangesWith(randomOrder = newVal)) }
    }

    fun showAddQuestionSheet(editIndex: Int? = null) {
        _uiState.update { it.copy(showAddQuestionSheet = true, editingQuestionIndex = editIndex) }
    }

    fun hideAddQuestionSheet() {
        _uiState.update { it.copy(showAddQuestionSheet = false, editingQuestionIndex = null) }
    }

    fun addQuestion(question: QuestionUiModel) {
        val newQuestions = _uiState.value.questions + question
        _uiState.update {
            it.copy(
                questions = newQuestions,
                showAddQuestionSheet = false,
                editingQuestionIndex = null,
                hasUnsavedChanges = computeHasChangesWith(questions = newQuestions)
            )
        }
    }

    fun editQuestion(index: Int, question: QuestionUiModel) {
        val updated = _uiState.value.questions.toMutableList()
        updated[index] = question
        val newQuestions = updated.toList()
        _uiState.update {
            it.copy(
                questions = newQuestions,
                showAddQuestionSheet = false,
                editingQuestionIndex = null,
                hasUnsavedChanges = computeHasChangesWith(questions = newQuestions)
            )
        }
    }

    fun deleteQuestion(index: Int) {
        val newQuestions = _uiState.value.questions.toMutableList().apply { removeAt(index) }.toList()
        _uiState.update { it.copy(questions = newQuestions, hasUnsavedChanges = computeHasChangesWith(questions = newQuestions)) }
    }

    fun saveTemplate() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            viewModelScope.launch {
                _events.emit(CreateTemplateEvent.ShowToast(UiText.StringRes(R.string.fill_title_error)))
            }
            return
        }
        if (state.questions.isEmpty()) {
            viewModelScope.launch {
                _events.emit(CreateTemplateEvent.ShowToast(UiText.StringRes(R.string.add_question_error)))
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val questionMaps = mapQuestionsToDto(state.questions)

            val result = if (isEditMode) {
                quizRepository.updateTemplate(
                    id = templateId!!,
                    title = state.title,
                    quizType = state.quizType,
                    questions = questionMaps,
                    randomOrder = state.randomOrder
                )
            } else {
                quizRepository.createTemplate(
                    title = state.title,
                    quizType = state.quizType,
                    questions = questionMaps,
                    randomOrder = state.randomOrder
                ).let { r ->
                    when (r) {
                        is AppResult.Ok -> AppResult.Ok(Unit)
                        is AppResult.Err -> AppResult.Err(r.error)
                    }
                }
            }

            when (result) {
                is AppResult.Ok -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CreateTemplateEvent.NavigateBack)
                }
                is AppResult.Err -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CreateTemplateEvent.ShowToast(result.error.toUiText()))
                }
            }
        }
    }

    fun deleteTemplate() {
        if (!isEditMode) return
        _uiState.update { it.copy(isLoading = true, showDeleteConfirmDialog = false) }
        viewModelScope.launch {
            val result = quizRepository.deleteTemplate(templateId!!)
            when (result) {
                is AppResult.Ok -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CreateTemplateEvent.NavigateBack)
                }
                is AppResult.Err -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CreateTemplateEvent.ShowToast(result.error.toUiText()))
                }
            }
        }
    }

    fun generateFromAi(prompt: String) {
        _uiState.update { it.copy(isLoadingTemplate = true) }
        viewModelScope.launch {
            val result = quizRepository.generateTemplate(prompt)
            when (result) {
                is AppResult.Ok -> {
                    val detail = result.value
                    val questions = detail.questions.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(
                            title = detail.title,
                            quizType = detail.quizType,
                            randomOrder = detail.randomOrder,
                            questions = questions,
                            isLoadingTemplate = false,
                            hasUnsavedChanges = computeHasChangesWith(
                                title = detail.title,
                                quizType = detail.quizType,
                                randomOrder = detail.randomOrder,
                                questions = questions
                            )
                        )
                    }
                }
                is AppResult.Err -> {
                    _uiState.update { it.copy(isLoadingTemplate = false) }
                    _events.emit(CreateTemplateEvent.ShowToast(result.error.toUiText()))
                }
            }
        }
    }

    fun generateMoreQuestions(prompt: String) {
        _uiState.update { it.copy(isGeneratingQuestions = true, showGenerateQuestionsSheet = false) }
        viewModelScope.launch {
            val result = quizRepository.generateQuestions(templateId ?: "", prompt)
            when (result) {
                is AppResult.Ok -> {
                    val newQuestions = _uiState.value.questions + result.value.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(
                            questions = newQuestions,
                            isGeneratingQuestions = false,
                            hasUnsavedChanges = computeHasChangesWith(questions = newQuestions)
                        )
                    }
                }
                is AppResult.Err -> {
                    _uiState.update { it.copy(isGeneratingQuestions = false) }
                    _events.emit(CreateTemplateEvent.ShowToast(result.error.toUiText()))
                }
            }
        }
    }

    fun onBackAttempt() {
        if (_uiState.value.hasUnsavedChanges) {
            _uiState.update { it.copy(showUnsavedChangesDialog = true) }
        } else {
            viewModelScope.launch { _events.emit(CreateTemplateEvent.NavigateBack) }
        }
    }

    fun onConfirmBack() {
        viewModelScope.launch { _events.emit(CreateTemplateEvent.NavigateBack) }
    }

    fun onDismissUnsavedChangesDialog() {
        _uiState.update { it.copy(showUnsavedChangesDialog = false) }
    }

    fun onShowDeleteConfirmDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false) }
    }

    fun onShowGenerateQuestionsSheet() {
        _uiState.update { it.copy(showGenerateQuestionsSheet = true) }
    }

    fun onDismissGenerateQuestionsSheet() {
        _uiState.update { it.copy(showGenerateQuestionsSheet = false) }
    }

    fun onGenerateQuestionsPromptChange(prompt: String) {
        _uiState.update { it.copy(generateQuestionsPrompt = prompt) }
    }

    private fun mapQuestionsToDto(questions: List<QuestionUiModel>): List<Map<String, Any?>> {
        return questions.map { q ->
            val correctAnswer: Any = when (q.type) {
                QuestionType.SINGLE -> q.correctOptionIndices.firstOrNull() ?: 0
                QuestionType.MULTIPLE -> q.correctOptionIndices.sorted()
                QuestionType.OPEN -> q.correctAnswer
            }
            mapOf(
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
    }

    private fun TemplateQuestion.toUiModel(): QuestionUiModel {
        val questionType = when (type) {
            "single" -> QuestionType.SINGLE
            "multiple" -> QuestionType.MULTIPLE
            else -> QuestionType.OPEN
        }
        val opts = options?.let {
            val m = it.toMutableList()
            while (m.size < 4) m.add("")
            m.toList()
        } ?: listOf("", "", "", "")

        return QuestionUiModel(
            text = text,
            type = questionType,
            options = opts,
            correctAnswer = if (questionType == QuestionType.OPEN) correctAnswer else "",
            correctOptionIndices = when (questionType) {
                QuestionType.SINGLE -> correctAnswer.toIntOrNull()?.let { setOf(it) } ?: emptySet()
                QuestionType.MULTIPLE -> correctAnswer.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
                QuestionType.OPEN -> emptySet()
            },
            maxScore = maxScore,
            timeLimitSec = timeLimitSec ?: 30
        )
    }
}
