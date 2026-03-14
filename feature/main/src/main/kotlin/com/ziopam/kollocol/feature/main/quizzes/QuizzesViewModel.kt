package com.ziopam.kollocol.feature.main.quizzes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

@HiltViewModel
class QuizzesViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedTabIndex = MutableStateFlow(0)

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

    private fun filterQuizzesByQuery(quizzes: List<QuizInfo>, query: String): List<QuizInfo> {
        if (query.isBlank()) {
            return quizzes
        }
        return quizzes.filter { quiz ->
            quiz.title.contains(query, ignoreCase = true)
        }
    }
}