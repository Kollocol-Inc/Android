package com.ziopam.kollocol.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.repository.NotificationRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
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
    val avatarUrl: String? = null,
    val quizCode: String = "",
    val participatingQuizzes: List<QuizInfo> = emptyList(),
    val hostingQuizzes: List<QuizInfo> = emptyList(),
    val unreadNotificationsCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val personalRepository: PersonalRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val quizCode = MutableStateFlow("")
    private val _avatarCacheBuster = MutableStateFlow(System.currentTimeMillis())
    private val _unreadCount = MutableStateFlow(0)

    val uiState = combine(
        personalRepository.getUserFlow(),
        quizCode,
        quizRepository.participatingQuizzes,
        quizRepository.runningQuizzes,
        combine(_avatarCacheBuster, _unreadCount) { buster, unread -> buster to unread }
    ) { user, quizCode, participating, hosting, (buster, unread) ->
        HomeUiState(
            personName = "${user.firstName} ${user.lastName}".trim(),
            avatarUrl = user.avatarUrl?.let { url -> "$url?t=$buster" },
            quizCode = quizCode,
            participatingQuizzes = participating,
            hostingQuizzes = hosting,
            unreadNotificationsCount = unread
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        refresh()
    }

    fun onCodeChanged(code: String) {
        quizCode.value = code.take(6)
    }

    fun refresh() {
        viewModelScope.launch {
            val userResult = personalRepository.getUser()
            if (userResult is AppResult.Ok) {
                _avatarCacheBuster.value = System.currentTimeMillis()
            }
            quizRepository.getParticipatingQuizzes()
            quizRepository.getHostingQuizzes()
        }
        viewModelScope.launch {
            val result = notificationRepository.getUnreadCount()
            if (result is AppResult.Ok) {
                _unreadCount.value = result.value
            }
        }
    }
}
