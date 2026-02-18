package com.ziopam.kollocol.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalOnlineRepository
import com.ziopam.kollocol.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val personName: String = "",
    val participatingQuizzes: List<QuizInfo> = emptyList(),
    val hostingQuizzes: List<QuizInfo> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val personalRepository: PersonalOnlineRepository
) : ViewModel() {

    val uiState = combine(
        personalRepository.user,
        quizRepository.participatingQuizzes,
        quizRepository.hostingQuizzes,
    ) { user, participating, hosting ->
        HomeUiState(
            personName = formatPersonName(user),
            participatingQuizzes = participating,
            hostingQuizzes = hosting,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )


    // TODO Добавить обработку ошибок
    fun refresh() {
        viewModelScope.launch {
            personalRepository.getUser()
            quizRepository.getParticipatingQuizzes()
            quizRepository.getHostingQuizzes()
        }
    }

    private fun formatPersonName(user: User): String {
        return "${user.firstName} ${user.lastName}".trim()
    }
}