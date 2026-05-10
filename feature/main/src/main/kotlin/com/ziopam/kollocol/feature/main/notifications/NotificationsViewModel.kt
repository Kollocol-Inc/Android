package com.ziopam.kollocol.feature.main.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.Notification
import com.ziopam.kollocol.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    private val sentReadIds = mutableSetOf<String>()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val result = notificationRepository.getNotifications(limit = 50, offset = 0)
            if (result is AppResult.Ok) {
                val notifications = result.value
                    .filter { !it.requiresAction || !it.isRead }
                    .sortedByDescending { parseInstantOrMin(it.createdAt) }
                sentReadIds.addAll(notifications.filter { it.isRead }.map { it.id })
                _uiState.update { it.copy(notifications = notifications, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onVisibleNotifications(visibleIds: List<String>) {
        val toMark = visibleIds.filter { id ->
            id !in sentReadIds && _uiState.value.notifications.any { notification ->
                notification.id == id &&
                        !notification.isRead &&
                        !notification.requiresAction
            }
        }
        if (toMark.isEmpty()) return

        sentReadIds.addAll(toMark)
        _uiState.update { state ->
            state.copy(notifications = state.notifications.map {
                if (it.id in toMark) it.copy(isRead = true) else it
            })
        }

        viewModelScope.launch {
            notificationRepository.markAsRead(toMark)
        }
    }

    fun markAllAsRead() {
        val toMark = _uiState.value.notifications
            .filter { !it.isRead && !it.requiresAction }
            .map { it.id }
        if (toMark.isEmpty()) return

        sentReadIds.addAll(toMark)
        _uiState.update { state ->
            state.copy(notifications = state.notifications.map {
                if (it.id in toMark) it.copy(isRead = true) else it
            })
        }

        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun acceptGroupInvite(groupId: String, notificationId: String) {
        viewModelScope.launch {
            notificationRepository.acceptGroupInvite(groupId)
            _uiState.update { state ->
                state.copy(notifications = state.notifications.filter { it.id != notificationId })
            }
        }
    }

    fun declineGroupInvite(groupId: String, notificationId: String) {
        viewModelScope.launch {
            notificationRepository.declineGroupInvite(groupId)
            _uiState.update { state ->
                state.copy(notifications = state.notifications.filter { it.id != notificationId })
            }
        }
    }

    private fun parseInstantOrMin(createdAt: String): Instant =
        runCatching { Instant.parse(createdAt) }.getOrElse { Instant.MIN }
}
