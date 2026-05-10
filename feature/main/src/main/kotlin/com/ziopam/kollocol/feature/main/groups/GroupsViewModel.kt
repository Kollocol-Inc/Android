package com.ziopam.kollocol.feature.main.groups

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupSheetState(
    val groupId: String? = null,
    val name: String = "",
    val description: String = "",
    val avatarUrl: String? = null,
    val pendingAvatarUri: Uri? = null,
    val emails: List<String> = emptyList(),
    val isLoading: Boolean = false
)

data class GroupsUiState(
    val memberGroups: List<Group> = emptyList(),
    val createdGroups: List<Group> = emptyList(),
    val pendingInvitations: List<Group> = emptyList(),
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val isLoading: Boolean = false,
    val invitationsExpanded: Boolean = false,
    val showGroupSheet: Boolean = false,
    val groupSheetState: GroupSheetState = GroupSheetState()
) {
    val filteredGroups: List<Group>
        get() {
            val list = if (selectedTabIndex == 0) memberGroups else createdGroups
            return if (searchQuery.isBlank()) list
            else list.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    val displayedInvitations: List<Group>
        get() = if (invitationsExpanded || pendingInvitations.size <= 1) pendingInvitations
        else pendingInvitations.take(1)
}

sealed class GroupsEvent {
    data class ShowError(val message: UiText) : GroupsEvent()
    data class ShowToast(val message: UiText) : GroupsEvent()
    data class NavigateToDetail(val groupId: String) : GroupsEvent()
}

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GroupsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<GroupsEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                groupRepository.memberGroups,
                groupRepository.createdGroups
            ) { member, created -> member to created }
                .collect { (member, created) ->
                    _state.update { it.copy(memberGroups = member, createdGroups = created) }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = groupRepository.syncGroups()
            val hasCachedData = _state.value.memberGroups.isNotEmpty() || _state.value.createdGroups.isNotEmpty()
            if (result is AppResult.Err && !(result.error is AppError.NoInternet && hasCachedData)) {
                _events.send(GroupsEvent.ShowError(result.error.toUiText()))
            }

            _state.update { it.copy(isLoading = false) }
            loadPendingInvitations()
        }
    }

    private suspend fun loadPendingInvitations() {
        val result = groupRepository.getInviteeGroups()
        if (result is AppResult.Ok) {
            _state.update { it.copy(pendingInvitations = result.value) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
    }

    fun onToggleInvitationsExpanded() {
        _state.update { it.copy(invitationsExpanded = !it.invitationsExpanded) }
    }

    fun onAcceptInvitation(groupId: String) {
        viewModelScope.launch {
            val result = groupRepository.acceptGroupInvite(groupId)
            when (result) {
                is AppResult.Ok -> {
                    _state.update { s ->
                        s.copy(pendingInvitations = s.pendingInvitations.filter { it.id != groupId })
                    }
                    _events.send(GroupsEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.invite_accepted)))
                    refresh()
                }
                is AppResult.Err -> _events.send(GroupsEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onDeclineInvitation(groupId: String) {
        viewModelScope.launch {
            val result = groupRepository.declineGroupInvite(groupId)
            when (result) {
                is AppResult.Ok -> {
                    _state.update { s ->
                        s.copy(pendingInvitations = s.pendingInvitations.filter { it.id != groupId })
                    }
                    _events.send(GroupsEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.invite_declined)))
                }
                is AppResult.Err -> _events.send(GroupsEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onGroupClick(group: Group) {
        viewModelScope.launch {
            _events.send(GroupsEvent.NavigateToDetail(group.id))
        }
    }

    fun onCreateGroupClick() {
        _state.update {
            it.copy(
                showGroupSheet = true,
                groupSheetState = GroupSheetState()
            )
        }
    }

    fun onDismissGroupSheet() {
        _state.update { it.copy(showGroupSheet = false) }
    }

    fun onSheetNameChange(name: String) {
        _state.update { it.copy(groupSheetState = it.groupSheetState.copy(name = name)) }
    }

    fun onSheetDescriptionChange(description: String) {
        _state.update { it.copy(groupSheetState = it.groupSheetState.copy(description = description)) }
    }

    fun onSheetAddEmail() {
        _state.update {
            it.copy(
                groupSheetState = it.groupSheetState.copy(
                    emails = it.groupSheetState.emails + ""
                )
            )
        }
    }

    fun onSheetEmailChange(index: Int, email: String) {
        _state.update { s ->
            val emails = s.groupSheetState.emails.toMutableList()
            if (index in emails.indices) emails[index] = email
            s.copy(groupSheetState = s.groupSheetState.copy(emails = emails))
        }
    }

    fun onSheetRemoveEmail(index: Int) {
        _state.update { s ->
            val emails = s.groupSheetState.emails.toMutableList()
            if (index in emails.indices) emails.removeAt(index)
            s.copy(groupSheetState = s.groupSheetState.copy(emails = emails))
        }
    }

    fun onSheetAvatarSelected(uri: Uri) {
        _state.update { it.copy(groupSheetState = it.groupSheetState.copy(pendingAvatarUri = uri)) }
    }

    fun onSheetAvatarDeleted() {
        _state.update {
            it.copy(
                groupSheetState = it.groupSheetState.copy(
                    avatarUrl = null,
                    pendingAvatarUri = null
                )
            )
        }
    }

    fun onCreateGroup() {
        val sheetState = _state.value.groupSheetState
        if (sheetState.name.isBlank()) {
            viewModelScope.launch {
                _events.send(GroupsEvent.ShowError(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.group_name_empty_error)))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(groupSheetState = it.groupSheetState.copy(isLoading = true)) }

            var finalAvatarUrl: String? = sheetState.avatarUrl
            val pendingUri = sheetState.pendingAvatarUri
            if (pendingUri != null) {
                val uploadResult = groupRepository.uploadGroupAvatar(pendingUri)
                if (uploadResult is AppResult.Ok) {
                    finalAvatarUrl = uploadResult.value
                } else if (uploadResult is AppResult.Err) {
                    _state.update { it.copy(groupSheetState = it.groupSheetState.copy(isLoading = false)) }
                    _events.send(GroupsEvent.ShowError(uploadResult.error.toUiText()))
                    return@launch
                }
            }

            val validEmails = sheetState.emails.filter { isValidEmail(it) }
            val result = groupRepository.createGroup(
                name = sheetState.name.trim(),
                description = sheetState.description.trim(),
                avatarUrl = finalAvatarUrl,
                memberEmails = validEmails
            )

            _state.update { it.copy(groupSheetState = it.groupSheetState.copy(isLoading = false)) }

            when (result) {
                is AppResult.Ok -> {
                    _state.update { it.copy(showGroupSheet = false) }
                    _events.send(GroupsEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.group_created)))
                    refresh()
                }
                is AppResult.Err -> _events.send(GroupsEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }
}
