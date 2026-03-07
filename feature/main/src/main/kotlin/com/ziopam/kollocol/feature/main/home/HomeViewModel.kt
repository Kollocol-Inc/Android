package com.ziopam.kollocol.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalOnlineRepository
import com.ziopam.kollocol.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val personName: String = "",
    val quizCode: String = "",
    val participatingQuizzes: List<QuizInfo> = emptyList(),
    val hostingQuizzes: List<QuizInfo> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val personalRepository: PersonalOnlineRepository
) : ViewModel() {
    private val quizCode = MutableStateFlow("")

    val uiState = combine(
        personalRepository.user,
        quizCode,
        quizRepository.participatingQuizzes,
        quizRepository.hostingQuizzes,
    ) { user, quizCode, participating, hosting ->
        HomeUiState(
            personName = formatPersonName(user),
            quizCode = quizCode,
            participatingQuizzes = participating,
            hostingQuizzes = hosting,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onCodeChanged(code: String) {
        quizCode.value = code.take(6)
    }

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