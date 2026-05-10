package com.ziopam.kollocol.feature.main.groups.detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.model.GroupDetail
import com.ziopam.kollocol.domain.model.GroupMember
import com.ziopam.kollocol.domain.repository.GroupRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
import com.ziopam.kollocol.feature.main.groups.GroupSheetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupDetailUiState(
    val detail: GroupDetail? = null,
    val isOwner: Boolean = false,
    val currentUserEmail: String = "",
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val showLeaveConfirm: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val memberToKick: GroupMember? = null,
    val inviteToCancel: GroupMember? = null,
    val showAvatarMenu: Boolean = false,
    val showEditSheet: Boolean = false,
    val editSheetState: GroupSheetState = GroupSheetState(),
    val showInviteSheet: Boolean = false,
    val avatarCacheBuster: Long = System.currentTimeMillis()
) {
    val filteredMembers
        get() = if (searchQuery.isBlank()) detail?.members.orEmpty()
        else detail?.members.orEmpty().filter {
            "${it.firstName} ${it.lastName}".contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
        }

    val filteredInvited
        get() = if (searchQuery.isBlank()) detail?.invitedUsers.orEmpty()
        else detail?.invitedUsers.orEmpty().filter {
            "${it.firstName} ${it.lastName}".contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
        }
}

sealed class GroupDetailEvent {
    data class ShowError(val message: UiText) : GroupDetailEvent()
    data class ShowToast(val message: UiText) : GroupDetailEvent()
    object NavigateBack : GroupDetailEvent()
}

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
    private val personalRepository: PersonalRepository
) : ViewModel() {

    private val groupId: String = checkNotNull(savedStateHandle["groupId"])

    private val _state = MutableStateFlow(GroupDetailUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<GroupDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = groupRepository.getGroupDetail(groupId)
            _state.update { it.copy(isLoading = false) }

            when (result) {
                is AppResult.Ok -> {
                    val detail = result.value
                    val currentEmail = personalRepository.getUserFlow().first().email
                    val currentUserMember = detail.members.find { it.email == currentEmail }
                    val isOwner = currentUserMember?.userId == detail.ownerId

                    _state.update {
                        it.copy(
                            detail = detail,
                            isOwner = isOwner,
                            currentUserEmail = currentEmail,
                            avatarCacheBuster = System.currentTimeMillis()
                        )
                    }
                }
                is AppResult.Err -> _events.send(GroupDetailEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onAvatarMenuToggle() {
        _state.update { it.copy(showAvatarMenu = !it.showAvatarMenu) }
    }

    fun onAvatarMenuDismiss() {
        _state.update { it.copy(showAvatarMenu = false) }
    }

    fun onLeaveClick() {
        _state.update { it.copy(showLeaveConfirm = true) }
    }

    fun onConfirmLeave() {
        _state.update { it.copy(showLeaveConfirm = false) }
        viewModelScope.launch {
            val result = groupRepository.leaveGroup(groupId)
            when (result) {
                is AppResult.Ok -> {
                    _events.send(GroupDetailEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.group_left)))
                    _events.send(GroupDetailEvent.NavigateBack)
                }
                is AppResult.Err -> _events.send(GroupDetailEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onDismissLeaveConfirm() {
        _state.update { it.copy(showLeaveConfirm = false) }
    }

    fun onDeleteClick() {
        _state.update { it.copy(showDeleteConfirm = true, showAvatarMenu = false) }
    }

    fun onConfirmDelete() {
        _state.update { it.copy(showDeleteConfirm = false) }
        viewModelScope.launch {
            val result = groupRepository.deleteGroup(groupId)
            when (result) {
                is AppResult.Ok -> {
                    _events.send(GroupDetailEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.group_deleted)))
                    _events.send(GroupDetailEvent.NavigateBack)
                }
                is AppResult.Err -> _events.send(GroupDetailEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onDismissDeleteConfirm() {
        _state.update { it.copy(showDeleteConfirm = false) }
    }

    fun onEditClick() {
        val detail = _state.value.detail ?: return
        _state.update {
            it.copy(
                showEditSheet = true,
                showAvatarMenu = false,
                editSheetState = GroupSheetState(
                    groupId = groupId,
                    name = detail.name,
                    description = detail.description,
                    avatarUrl = detail.avatarUrl?.let { url -> "$url?t=${it.avatarCacheBuster}" }
                )
            )
        }
    }

    fun onDismissEditSheet() {
        _state.update { it.copy(showEditSheet = false) }
    }

    fun onEditSheetNameChange(name: String) {
        _state.update { it.copy(editSheetState = it.editSheetState.copy(name = name)) }
    }

    fun onEditSheetDescriptionChange(description: String) {
        _state.update { it.copy(editSheetState = it.editSheetState.copy(description = description)) }
    }

    fun onEditSheetAvatarSelected(uri: Uri) {
        _state.update { it.copy(editSheetState = it.editSheetState.copy(pendingAvatarUri = uri)) }
    }

    fun onEditSheetAvatarDeleted() {
        _state.update {
            it.copy(editSheetState = it.editSheetState.copy(avatarUrl = null, pendingAvatarUri = null))
        }
    }

    fun onSaveGroup() {
        val sheetState = _state.value.editSheetState
        if (sheetState.name.isBlank()) {
            viewModelScope.launch {
                _events.send(GroupDetailEvent.ShowError(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.group_name_empty_error)))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(editSheetState = it.editSheetState.copy(isLoading = true)) }

            var finalAvatarUrl: String? = _state.value.detail?.avatarUrl
            val pendingUri = sheetState.pendingAvatarUri

            if (sheetState.avatarUrl == null && sheetState.pendingAvatarUri == null) {
                finalAvatarUrl = null
            } else if (pendingUri != null) {
                val uploadResult = groupRepository.uploadGroupAvatar(pendingUri)
                if (uploadResult is AppResult.Ok) {
                    finalAvatarUrl = uploadResult.value
                } else if (uploadResult is AppResult.Err) {
                    _state.update { it.copy(editSheetState = it.editSheetState.copy(isLoading = false)) }
                    _events.send(GroupDetailEvent.ShowError(uploadResult.error.toUiText()))
                    return@launch
                }
            }

            val result = groupRepository.updateGroup(
                id = groupId,
                name = sheetState.name.trim(),
                description = sheetState.description.trim(),
                avatarUrl = finalAvatarUrl
            )

            _state.update { it.copy(editSheetState = it.editSheetState.copy(isLoading = false)) }

            when (result) {
                is AppResult.Ok -> {
                    _state.update { it.copy(showEditSheet = false) }
                    _events.send(GroupDetailEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.group_updated)))
                    loadDetail()
                }
                is AppResult.Err -> _events.send(GroupDetailEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onKickMemberClick(member: GroupMember) {
        _state.update { it.copy(memberToKick = member) }
    }

    fun onConfirmKickMember() {
        val member = _state.value.memberToKick ?: return
        _state.update { it.copy(memberToKick = null) }
        viewModelScope.launch {
            val result = groupRepository.kickMembers(groupId, listOf(member.email))
            when (result) {
                is AppResult.Ok -> {
                    _events.send(GroupDetailEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.member_kicked)))
                    loadDetail()
                }
                is AppResult.Err -> _events.send(GroupDetailEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onDismissKickConfirm() {
        _state.update { it.copy(memberToKick = null) }
    }

    fun onCancelInviteClick(member: GroupMember) {
        _state.update { it.copy(inviteToCancel = member) }
    }

    fun onConfirmCancelInvite() {
        val member = _state.value.inviteToCancel ?: return
        _state.update { it.copy(inviteToCancel = null) }
        viewModelScope.launch {
            val result = groupRepository.kickMembers(groupId, listOf(member.email))
            when (result) {
                is AppResult.Ok -> {
                    _events.send(GroupDetailEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.invite_cancelled)))
                    loadDetail()
                }
                is AppResult.Err -> _events.send(GroupDetailEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun onDismissCancelInvite() {
        _state.update { it.copy(inviteToCancel = null) }
    }

    fun onInviteMembersClick() {
        _state.update { it.copy(showInviteSheet = true) }
    }

    fun onDismissInviteSheet() {
        _state.update { it.copy(showInviteSheet = false) }
    }

    fun onInviteMembers(emails: List<String>) {
        val validEmails = emails.filter { android.util.Patterns.EMAIL_ADDRESS.matcher(it.trim()).matches() }
        if (validEmails.isEmpty()) return

        viewModelScope.launch {
            val result = groupRepository.inviteMembers(groupId, validEmails)
            when (result) {
                is AppResult.Ok -> {
                    _state.update { it.copy(showInviteSheet = false) }
                    _events.send(GroupDetailEvent.ShowToast(UiText.StringRes(com.ziopam.kollocol.feature.main.R.string.members_invited_success)))
                    loadDetail()
                }
                is AppResult.Err -> _events.send(GroupDetailEvent.ShowError(result.error.toUiText()))
            }
        }
    }

    fun getGroupId(): String = groupId
}
