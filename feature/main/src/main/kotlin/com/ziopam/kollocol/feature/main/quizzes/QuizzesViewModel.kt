package com.ziopam.kollocol.feature.main.quizzes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.repository.GroupRepository
import com.ziopam.kollocol.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

data class MyQuizzesState(
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val runningQuizzes: List<QuizInfo> = emptyList(),
    val pendingQuizzes: List<QuizInfo> = emptyList(),
    val reviewedQuizzes: List<QuizInfo> = emptyList(),
)

data class TemplatesState(
    val templates: List<QuizInfo> = emptyList()
)

data class StartQuizSheetState(
    val template: QuizInfo? = null,
    val title: String = "",
    val deadlineDateMillis: Long? = null,
    val deadlineHour: Int? = null,
    val deadlineMinute: Int? = null,
    val isLoading: Boolean = false,
    val selectedGroup: Group? = null,
    val ownedGroups: List<Group> = emptyList(),
    val groupDropdownExpanded: Boolean = false,
    val groupSearchQuery: String = ""
)

sealed class QuizzesEvent {
    data class ShowError(val message: String) : QuizzesEvent()
    object QuizStarted : QuizzesEvent()
}

@HiltViewModel
class QuizzesViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedTabIndex = MutableStateFlow(0)

    private val _startQuizState = MutableStateFlow(StartQuizSheetState())
    val startQuizState = _startQuizState.asStateFlow()

    private val _events = Channel<QuizzesEvent>()
    val events = _events.receiveAsFlow()

    val myQuizzesState = combine(
        searchQuery,
        selectedTabIndex,
        quizRepository.runningQuizzes,
        quizRepository.pendingQuizzes,
        quizRepository.reviewedQuizzes
    ) { query, tabIndex, running, pending, reviewed ->
        MyQuizzesState(
            searchQuery = query,
            selectedTabIndex = tabIndex,
            runningQuizzes = filterQuizzesByQuery(running, query),
            pendingQuizzes = filterQuizzesByQuery(pending, query),
            reviewedQuizzes = filterQuizzesByQuery(reviewed, query)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyQuizzesState()
    )
    
    val templatesState = combine(
        searchQuery,
        quizRepository.templates
    ) { query, templates ->
        TemplatesState(templates = filterQuizzesByQuery(templates, query))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TemplatesState()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onTabSelected(index: Int) {
        selectedTabIndex.value = index
    }

    fun refresh() {
        viewModelScope.launch {
            quizRepository.getHostingQuizzes(status = null)
            quizRepository.getHostingQuizzes(status = "pending_review")
            quizRepository.getHostingQuizzes(status = "reviewed")
            quizRepository.getTemplates()
        }
    }
    
    fun refreshTemplates() {
        viewModelScope.launch {
            quizRepository.getTemplates()
        }
    }

    fun onStartClick(template: QuizInfo) {
        _startQuizState.value = StartQuizSheetState(template = template, title = template.title)
        viewModelScope.launch {
            groupRepository.syncGroups()
            val groups = groupRepository.createdGroups.first()
            _startQuizState.update { it.copy(ownedGroups = groups) }
        }
    }

    fun onDismissStartQuiz() {
        _startQuizState.value = StartQuizSheetState()
    }

    fun onStartQuizTitleChange(title: String) {
        _startQuizState.update { it.copy(title = title) }
    }

    fun onStartQuizDeadlineDateChange(millis: Long) {
        _startQuizState.update { it.copy(deadlineDateMillis = millis) }
    }

    fun onStartQuizDeadlineTimeChange(hour: Int, minute: Int) {
        _startQuizState.update { it.copy(deadlineHour = hour, deadlineMinute = minute) }
    }

    fun onGroupDropdownToggle() {
        _startQuizState.update { it.copy(groupDropdownExpanded = !it.groupDropdownExpanded) }
    }

    fun onGroupDropdownDismiss() {
        _startQuizState.update { it.copy(groupDropdownExpanded = false, groupSearchQuery = "") }
    }

    fun onGroupSearchQueryChange(query: String) {
        _startQuizState.update { it.copy(groupSearchQuery = query) }
    }

    fun onGroupSelected(group: Group?) {
        _startQuizState.update { it.copy(selectedGroup = group, groupDropdownExpanded = false, groupSearchQuery = "") }
    }

    fun startQuiz() {
        val state = _startQuizState.value
        val template = state.template ?: return

        viewModelScope.launch {
            _startQuizState.update { it.copy(isLoading = true) }

            val deadline = buildDeadline(state)

            val result = quizRepository.createInstance(
                templateId = template.id,
                title = state.title,
                deadline = deadline,
                groupId = state.selectedGroup?.id
            )

            when (result) {
                is AppResult.Ok -> {
                    _startQuizState.value = StartQuizSheetState()
                    _events.send(QuizzesEvent.QuizStarted)
                    refresh()
                }
                is AppResult.Err -> {
                    _startQuizState.update { it.copy(isLoading = false) }
                    val message = when (val err = result.error) {
                        is AppError.BadRequest -> err.message ?: "Ошибка запроса"
                        AppError.NoInternet -> "Нет подключения к интернету"
                        AppError.Timeout -> "Превышено время ожидания"
                        else -> "Не удалось запустить квиз"
                    }
                    _events.send(QuizzesEvent.ShowError(message))
                }
            }
        }
    }

    private fun buildDeadline(state: StartQuizSheetState): String? {
        val dateMillis = state.deadlineDateMillis ?: return null
        val hour = state.deadlineHour ?: return null
        val minute = state.deadlineMinute ?: return null
        val localDate = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.of("UTC")).toLocalDate()
        return ZonedDateTime.of(
            localDate.atTime(hour, minute),
            ZoneId.systemDefault()
        ).toInstant().toString()
    }

    private fun filterQuizzesByQuery(quizzes: List<QuizInfo>, query: String): List<QuizInfo> {
        if (query.isBlank()) {
            return quizzes
        }
        return quizzes.filter { quiz ->
            quiz.title.contains(query, ignoreCase = true)
        }
    }
}