package com.ziopam.kollocol.feature.main.groups

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.uiText.asString
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.feature.main.MainScaffoldPreview
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.groups.components.GroupCard
import com.ziopam.kollocol.feature.main.groups.components.GroupSheet
import com.ziopam.kollocol.feature.main.groups.components.GroupsAbove
import com.ziopam.kollocol.feature.main.groups.components.InvitationsSection

@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GroupsEvent.ShowError -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                is GroupsEvent.ShowToast -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                is GroupsEvent.NavigateToDetail -> onNavigateToDetail(event.groupId)
            }
        }
    }

    if (state.showGroupSheet) {
        val sheet = state.groupSheetState
        GroupSheet(
            isEditing = sheet.groupId != null,
            name = sheet.name,
            description = sheet.description,
            avatarUrl = sheet.avatarUrl,
            pendingAvatarUri = sheet.pendingAvatarUri,
            emails = sheet.emails,
            isLoading = sheet.isLoading,
            onDismiss = viewModel::onDismissGroupSheet,
            onNameChange = viewModel::onSheetNameChange,
            onDescriptionChange = viewModel::onSheetDescriptionChange,
            onAvatarSelected = viewModel::onSheetAvatarSelected,
            onAvatarDeleted = viewModel::onSheetAvatarDeleted,
            onAddEmail = viewModel::onSheetAddEmail,
            onEmailChange = viewModel::onSheetEmailChange,
            onRemoveEmail = viewModel::onSheetRemoveEmail,
            onSubmit = viewModel::onCreateGroup,
            onAvatarError = { msg ->
                Toast.makeText(context, msg.asString(context), Toast.LENGTH_SHORT).show()
            }
        )
    }

    GroupsScreen(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onTabSelected = viewModel::onTabSelected,
        onToggleInvitationsExpanded = viewModel::onToggleInvitationsExpanded,
        onAcceptInvitation = viewModel::onAcceptInvitation,
        onDeclineInvitation = viewModel::onDeclineInvitation,
        onGroupClick = viewModel::onGroupClick,
        onCreateGroupClick = viewModel::onCreateGroupClick
    )
}

@Composable
private fun GroupsScreen(
    state: GroupsUiState,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onToggleInvitationsExpanded: () -> Unit,
    onAcceptInvitation: (String) -> Unit,
    onDeclineInvitation: (String) -> Unit,
    onGroupClick: (Group) -> Unit,
    onCreateGroupClick: () -> Unit
) {
    LayoutWithLargeBottomCard(
        contentAbove = {
            GroupsAbove(
                searchQuery = state.searchQuery,
                selectedTabIndex = state.selectedTabIndex,
                onSearchQueryChange = onSearchQueryChange,
                onTabSelected = onTabSelected,
                onCreateClick = onCreateGroupClick
            )
        },
        content = {
            Crossfade(
                targetState = state.selectedTabIndex,
                animationSpec = tween(durationMillis = 300),
                label = "groups_crossfade"
            ) { _ ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.height(0.5.dp))
                    if (state.selectedTabIndex == 0 && state.pendingInvitations.isNotEmpty()) {
                        InvitationsSection(
                            invitations = state.pendingInvitations,
                            displayedInvitations = state.displayedInvitations,
                            expanded = state.invitationsExpanded,
                            onToggleExpanded = onToggleInvitationsExpanded,
                            onAccept = { onAcceptInvitation(it.id) },
                            onDecline = { onDeclineInvitation(it.id) }
                        )
                    }

                    val groups = state.filteredGroups
                    if (groups.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (state.selectedTabIndex == 0)
                                    stringResource(R.string.no_groups_member)
                                else
                                    stringResource(R.string.no_groups_owner),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        groups.forEach { group ->
                            GroupCard(
                                group = group,
                                onClick = { onGroupClick(group) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun GroupsScreenPreview() {
    val isInvitationsExpanded by mutableStateOf(false)

    MainScaffoldPreview {
        GroupsScreen(
            state = GroupsUiState(
                memberGroups = listOf(
                    Group("1", "Группа", "", null, 10, 2, "owner1"),
                    Group("2", "Группа 2", "описание", null, 5, 0, "owner2")
                ),
                pendingInvitations = listOf(
                    Group("3", "Новая группа", "описание", null, 3, 0, "owner3"),
                    Group("4", "Новая группа 2", "описание", null, 3, 0, "owner4")
                ),
                invitationsExpanded = isInvitationsExpanded
            ),
            onSearchQueryChange = {},
            onTabSelected = {},
            onToggleInvitationsExpanded = { isInvitationsExpanded },
            onAcceptInvitation = {},
            onDeclineInvitation = {},
            onGroupClick = {},
            onCreateGroupClick = {}
        )
    }
}
