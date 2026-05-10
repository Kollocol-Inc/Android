package com.ziopam.kollocol.feature.main.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.model.NotificationSettings
import com.ziopam.kollocol.domain.model.ThemeMode
import com.ziopam.kollocol.domain.repository.AuthRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
import com.ziopam.kollocol.domain.repository.SessionRepository
import com.ziopam.kollocol.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val avatarUrl: String? = null,
    val email: String = "",
    val notificationSettings: NotificationSettings? = null,
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val isLoading: Boolean = false
)

sealed class ProfileEvent {
    data class ShowError(val message: UiText) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val personalRepository: PersonalRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            personalRepository.getUserFlow().collect { user ->
                _state.update {
                    it.copy(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        avatarUrl = user.avatarUrl,
                        email = user.email
                    )
                }
            }
        }
        viewModelScope.launch {
            personalRepository.getThemeMode().collect { mode ->
                _state.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val userResult = userRepository.getUser()
            val notifResult = userRepository.getNotificationSettings()

            _state.update { it.copy(isLoading = false) }

            if (userResult is AppResult.Err) {
                _events.send(ProfileEvent.ShowError(userResult.error.toUiText()))
            }
            if (notifResult is AppResult.Ok) {
                _state.update { it.copy(notificationSettings = notifResult.value) }
            }
        }
    }

    fun onThemeChange(mode: ThemeMode) {
        viewModelScope.launch {
            personalRepository.setThemeMode(mode)
        }
    }

    fun onNewQuizzesToggle(enabled: Boolean) =
        updateNotifSettings { it.copy(newQuizzes = enabled) }

    fun onQuizResultsToggle(enabled: Boolean) =
        updateNotifSettings { it.copy(quizResults = enabled) }

    fun onGroupInvitesToggle(enabled: Boolean) =
        updateNotifSettings { it.copy(groupInvites = enabled) }

    fun onDeadlineReminderChange(value: String) =
        updateNotifSettings { it.copy(deadlineReminder = value) }

    private fun updateNotifSettings(transform: (NotificationSettings) -> NotificationSettings) {
        val current = _state.value.notificationSettings ?: return
        val updated = transform(current)
        _state.update { it.copy(notificationSettings = updated) }

        viewModelScope.launch {
            val result = userRepository.updateNotificationSettings(updated)
            if (result is AppResult.Err) {
                _events.send(ProfileEvent.ShowError(result.error.toUiText()))
                _state.update { it.copy(notificationSettings = current) }
            }
        }
    }

    fun onUpdateName(firstName: String, lastName: String) {
        viewModelScope.launch {
            val result = userRepository.updateName(firstName, lastName)
            when (result) {
                is AppResult.Ok -> _state.update { it.copy(firstName = firstName, lastName = lastName) }
                is AppResult.Err -> _events.send(ProfileEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onUploadAvatar(uri: Uri) {
        viewModelScope.launch {
            val result = userRepository.uploadAvatar(uri)
            if (result is AppResult.Err) {
                _events.send(ProfileEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onDeleteAvatar() {
        viewModelScope.launch {
            val result = userRepository.deleteAvatar()
            when (result) {
                is AppResult.Ok -> _state.update { it.copy(avatarUrl = null) }
                is AppResult.Err -> _events.send(ProfileEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = userRepository.deleteAccount()
            when (result) {
                is AppResult.Ok -> sessionRepository.clearSession()
                is AppResult.Err -> _events.send(ProfileEvent.ShowError(result.error.toUiText()))
            }
        }
    }
}
