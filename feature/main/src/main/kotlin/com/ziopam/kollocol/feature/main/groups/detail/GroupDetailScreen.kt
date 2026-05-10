package com.ziopam.kollocol.feature.main.groups.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.dialogs.DefaultDialog
import com.ziopam.kollocol.core.ui.input.SearchBar
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.core.ui.uiText.asString
import com.ziopam.kollocol.domain.model.GroupDetail
import com.ziopam.kollocol.domain.model.GroupMember
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.groups.components.GroupSheet
import com.ziopam.kollocol.feature.main.groups.detail.components.GroupDetailAbove
import com.ziopam.kollocol.feature.main.groups.detail.components.GroupMemberSection
import com.ziopam.kollocol.feature.main.groups.detail.components.InviteMembersSheet

@Composable
fun GroupDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GroupDetailEvent.ShowError -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                is GroupDetailEvent.ShowToast -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                is GroupDetailEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    if (state.showLeaveConfirm) {
        DefaultDialog(
            title = stringResource(R.string.leave_group_confirm_title),
            message = stringResource(R.string.leave_group_confirm_message),
            confirmText = stringResource(R.string.leave),
            cancelText = stringResource(R.string.cancel),
            onConfirm = viewModel::onConfirmLeave,
            onCancel = viewModel::onDismissLeaveConfirm
        )
    }

    if (state.showDeleteConfirm) {
        DefaultDialog(
            title = stringResource(R.string.delete_group_confirm_title),
            message = stringResource(R.string.delete_group_confirm_message),
            confirmText = stringResource(R.string.delete),
            cancelText = stringResource(R.string.cancel),
            onConfirm = viewModel::onConfirmDelete,
            onCancel = viewModel::onDismissDeleteConfirm
        )
    }

    state.memberToKick?.let { member ->
        DefaultDialog(
            title = stringResource(R.string.kick_member_confirm_title),
            message = stringResource(R.string.kick_member_confirm_message, "${member.firstName} ${member.lastName}"),
            confirmText = stringResource(R.string.confirm),
            cancelText = stringResource(R.string.cancel),
            onConfirm = viewModel::onConfirmKickMember,
            onCancel = viewModel::onDismissKickConfirm
        )
    }

    state.inviteToCancel?.let { member ->
        DefaultDialog(
            title = stringResource(R.string.cancel_invite_confirm_title),
            message = stringResource(R.string.cancel_invite_confirm_message, "${member.firstName} ${member.lastName}"),
            confirmText = stringResource(R.string.confirm),
            cancelText = stringResource(R.string.cancel),
            onConfirm = viewModel::onConfirmCancelInvite,
            onCancel = viewModel::onDismissCancelInvite
        )
    }

    if (state.showEditSheet) {
        val sheet = state.editSheetState
        GroupSheet(
            isEditing = true,
            name = sheet.name,
            description = sheet.description,
            avatarUrl = sheet.avatarUrl,
            pendingAvatarUri = sheet.pendingAvatarUri,
            emails = sheet.emails,
            isLoading = sheet.isLoading,
            onDismiss = viewModel::onDismissEditSheet,
            onNameChange = viewModel::onEditSheetNameChange,
            onDescriptionChange = viewModel::onEditSheetDescriptionChange,
            onAvatarSelected = viewModel::onEditSheetAvatarSelected,
            onAvatarDeleted = viewModel::onEditSheetAvatarDeleted,
            onAddEmail = {},
            onEmailChange = { _, _ -> },
            onRemoveEmail = {},
            onSubmit = viewModel::onSaveGroup,
            onAvatarError = { msg ->
                Toast.makeText(context, msg.asString(context), Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (state.showInviteSheet) {
        InviteMembersSheet(
            onDismiss = viewModel::onDismissInviteSheet,
            onInvite = viewModel::onInviteMembers
        )
    }

    GroupDetailScreen(
        state = state,
        onBackClick = onNavigateBack,
        onSearchChange = viewModel::onSearchQueryChange,
        onMenuToggle = viewModel::onAvatarMenuToggle,
        onMenuDismiss = viewModel::onAvatarMenuDismiss,
        onLeaveClick = viewModel::onLeaveClick,
        onEditClick = viewModel::onEditClick,
        onDeleteClick = viewModel::onDeleteClick,
        onKickMember = viewModel::onKickMemberClick,
        onCancelInvite = viewModel::onCancelInviteClick,
        onInviteMembersClick = viewModel::onInviteMembersClick
    )
}

@Composable
fun GroupDetailScreen(
    state: GroupDetailUiState,
    onBackClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onMenuToggle: () -> Unit,
    onMenuDismiss: () -> Unit,
    onLeaveClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onKickMember: (GroupMember) -> Unit,
    onCancelInvite: (GroupMember) -> Unit,
    onInviteMembersClick: () -> Unit
) {
    val detail = state.detail

    LayoutWithLargeBottomCard(
        scrollable = false,
        contentAbove = {
            GroupDetailAbove(
                groupName = detail?.name.orEmpty(),
                description = detail?.description.orEmpty(),
                avatarUrl = detail?.avatarUrl?.let { "$it?t=${state.avatarCacheBuster}" },
                isOwner = state.isOwner,
                showMenu = state.showAvatarMenu,
                onBackClick = onBackClick,
                onMenuToggle = onMenuToggle,
                onMenuDismiss = onMenuDismiss,
                onLeaveClick = onLeaveClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        },
        content = {
            if (state.isLoading || detail == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (state.isOwner) 72.dp else 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        SearchBar(
                            text = state.searchQuery,
                            onQueryChange = onSearchChange,
                            placeholder = stringResource(R.string.search_participants),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        GroupMemberSection(
                            title = stringResource(R.string.group_members_section),
                            members = state.filteredMembers,
                            isPending = false,
                            isOwner = state.isOwner,
                            currentUserEmail = state.currentUserEmail,
                            onActionClick = onKickMember
                        )

                        if (state.filteredInvited.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))

                            GroupMemberSection(
                                title = stringResource(R.string.group_invited_section),
                                members = state.filteredInvited,
                                isPending = true,
                                isOwner = state.isOwner,
                                currentUserEmail = state.currentUserEmail,
                                onActionClick = onCancelInvite
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.isOwner) {
                        DefaultButton(
                            text = stringResource(R.string.invite_member_button),
                            onClick = onInviteMembersClick,
                            isButtonEnabled = true,
                            isWidthLimited = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun GroupDetailPreview() {
    AppPreview {
        GroupDetailScreen(
            state = GroupDetailUiState(
                detail = GroupDetail(
                    id = "1",
                    name = "Биониклы",
                    description = "Описание группы",
                    avatarUrl = null,
                    memberCount = 4,
                    pendingCount = 2,
                    ownerId = "user1",
                    members = listOf(
                        GroupMember("user1", "user@example.com", "Арсений", "Потякин", null),
                        GroupMember("user2", "user2@example.com", "Иван", "Иванов", null)
                    ),
                    invitedUsers = listOf(
                        GroupMember("user3", "user3@example.com", "Петр", "Петров", null)
                    )
                ),
                isOwner = true
            ),
            onBackClick = {},
            onSearchChange = {},
            onMenuToggle = {},
            onMenuDismiss = {},
            onLeaveClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onKickMember = {},
            onCancelInvite = {},
            onInviteMembersClick = {}
        )
    }
}
