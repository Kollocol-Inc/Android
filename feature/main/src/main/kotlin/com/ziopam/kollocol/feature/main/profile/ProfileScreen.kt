package com.ziopam.kollocol.feature.main.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.avatar.rememberAvatarPicker
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.dialogs.DefaultDialog
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.core.ui.uiText.asString
import com.ziopam.kollocol.domain.model.NotificationSettings
import com.ziopam.kollocol.domain.model.ThemeMode
import com.ziopam.kollocol.feature.main.MainScaffoldPreview
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.profile.components.ProfileDangerZone
import com.ziopam.kollocol.feature.main.profile.components.ProfileEditNameDialog
import com.ziopam.kollocol.feature.main.profile.components.ProfileNotificationsSection
import com.ziopam.kollocol.feature.main.profile.components.ProfileSettingsSection
import com.ziopam.kollocol.feature.main.profile.components.ProfileUserCard
import java.io.File
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.ShowError ->
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    ProfileScreen(
        state = state,
        onThemeChange = viewModel::onThemeChange,
        onNewQuizzesToggle = viewModel::onNewQuizzesToggle,
        onQuizResultsToggle = viewModel::onQuizResultsToggle,
        onGroupInvitesToggle = viewModel::onGroupInvitesToggle,
        onDeadlineReminderChange = viewModel::onDeadlineReminderChange,
        onUpdateName = viewModel::onUpdateName,
        onUploadAvatar = viewModel::onUploadAvatar,
        onDeleteAvatar = viewModel::onDeleteAvatar,
        onLogout = viewModel::logout,
        onDeleteAccount = viewModel::deleteAccount
    )
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onThemeChange: (ThemeMode) -> Unit,
    onNewQuizzesToggle: (Boolean) -> Unit,
    onQuizResultsToggle: (Boolean) -> Unit,
    onGroupInvitesToggle: (Boolean) -> Unit,
    onDeadlineReminderChange: (String) -> Unit,
    onUpdateName: (String, String) -> Unit,
    onUploadAvatar: (Uri) -> Unit,
    onDeleteAvatar: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val context = LocalContext.current

    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }
    var showEditNameDialog by rememberSaveable { mutableStateOf(false) }
    var showAvatarMenu by remember { mutableStateOf(false) }
    var showDeadlineMenu by remember { mutableStateOf(false) }
    var showThemeMenu by remember { mutableStateOf(false) }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraImageUri?.let(onUploadAvatar)
    }

    val galleryPicker = rememberAvatarPicker(onAvatarSelected = onUploadAvatar, onError = {})

    if (showLogoutDialog) {
        DefaultDialog(
            title = stringResource(R.string.logout_confirm_title),
            message = stringResource(R.string.logout_confirm_message),
            confirmText = stringResource(R.string.logout),
            cancelText = stringResource(R.string.cancel),
            onConfirm = { onLogout(); showLogoutDialog = false },
            onCancel = { showLogoutDialog = false }
        )
    }

    if (showDeleteAccountDialog) {
        DefaultDialog(
            title = stringResource(R.string.delete_account_confirm_title),
            message = stringResource(R.string.delete_account_confirm_message),
            confirmText = stringResource(R.string.delete_account),
            cancelText = stringResource(R.string.cancel),
            onConfirm = { onDeleteAccount(); showDeleteAccountDialog = false },
            onCancel = { showDeleteAccountDialog = false }
        )
    }

    if (showEditNameDialog) {
        ProfileEditNameDialog(
            initialFirstName = state.firstName,
            initialLastName = state.lastName,
            onConfirm = { first, last -> onUpdateName(first, last); showEditNameDialog = false },
            onDismiss = { showEditNameDialog = false }
        )
        ExtraColors.negative
    }

    LayoutWithLargeBottomCard(
        scrollable = false,
        contentAbove = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.profile),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    CircleIconButton(
                        onClick = { showLogoutDialog = true },
                        icon = ImageVector.vectorResource(CoreR.drawable.exit),
                        contentDescription = stringResource(R.string.logout),
                        size = 48.dp,
                        iconModifier = Modifier.graphicsLayer { scaleX = -1f },
                        modifier = Modifier.align(Alignment.CenterEnd),
                        tint = Color(0xFFE53935)
                    )
                }
                ProfileUserCard(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    email = state.email,
                    avatarUrl = state.avatarUrl,
                    showMenu = showAvatarMenu,
                    onMenuDismiss = { showAvatarMenu = false },
                    onCardLongClick = { showAvatarMenu = true },
                    onEditName = { showAvatarMenu = false; showEditNameDialog = true },
                    onPickFromGallery = { showAvatarMenu = false; galleryPicker() },
                    onTakePhoto = {
                        showAvatarMenu = false
                        val file = File(context.cacheDir, "images/avatar_${System.currentTimeMillis()}.jpg")
                            .also { it.parentFile?.mkdirs() }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        cameraImageUri = uri
                        cameraLauncher.launch(uri)
                    },
                    onDeletePhoto = { showAvatarMenu = false; onDeleteAvatar() }
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "notif_header") {
                Text(
                    text = stringResource(R.string.notifications_section),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item(key = "notif_section") {
                ProfileNotificationsSection(
                    settings = state.notificationSettings,
                    showDeadlineMenu = showDeadlineMenu,
                    onDeadlineMenuDismiss = { showDeadlineMenu = false },
                    onDeadlineMenuOpen = { showDeadlineMenu = true },
                    onNewQuizzesToggle = onNewQuizzesToggle,
                    onQuizResultsToggle = onQuizResultsToggle,
                    onGroupInvitesToggle = onGroupInvitesToggle,
                    onDeadlineReminderChange = onDeadlineReminderChange
                )
            }

            item(key = "settings_header") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.settings_section),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item(key = "theme_section") {
                ProfileSettingsSection(
                    themeMode = state.themeMode,
                    showThemeMenu = showThemeMenu,
                    onThemeMenuDismiss = { showThemeMenu = false },
                    onThemeMenuOpen = { showThemeMenu = true },
                    onThemeChange = onThemeChange
                )
            }

            item(key = "danger_header") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.danger_zone_section),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item(key = "delete_account") {
                ProfileDangerZone(onDeleteAccount = { showDeleteAccountDialog = true })
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    MainScaffoldPreview {
        ProfileScreen(
            state = ProfileUiState(
                firstName = "Арсений",
                lastName = "Потякин",
                email = "example@kollocol.app",
                notificationSettings = NotificationSettings(
                    newQuizzes = true,
                    quizResults = true,
                    groupInvites = true,
                    deadlineReminder = "1h"
                ),
                themeMode = ThemeMode.LIGHT
            ),
            onThemeChange = {},
            onNewQuizzesToggle = {},
            onQuizResultsToggle = {},
            onGroupInvitesToggle = {},
            onDeadlineReminderChange = {},
            onUpdateName = { _, _ -> },
            onUploadAvatar = {},
            onDeleteAvatar = {},
            onLogout = {},
            onDeleteAccount = {}
        )
    }
}
