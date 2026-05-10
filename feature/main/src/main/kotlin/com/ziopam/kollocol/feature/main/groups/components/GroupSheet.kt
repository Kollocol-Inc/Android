package com.ziopam.kollocol.feature.main.groups.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.avatar.rememberAvatarPicker
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.feature.main.R
import java.io.File
import com.ziopam.kollocol.core.ui.R as CoreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GroupSheet(
    isEditing: Boolean,
    name: String,
    description: String,
    avatarUrl: String?,
    pendingAvatarUri: Uri?,
    emails: List<String>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAvatarSelected: (Uri) -> Unit,
    onAvatarDeleted: () -> Unit,
    onAddEmail: () -> Unit,
    onEmailChange: (Int, String) -> Unit,
    onRemoveEmail: (Int) -> Unit,
    onSubmit: () -> Unit,
    onAvatarError: (UiText) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAvatarMenu by remember { mutableStateOf(false) }

    val effectiveAvatarUrl = pendingAvatarUri?.toString() ?: avatarUrl

    val galleryLauncher = rememberAvatarPicker(
        onAvatarSelected = onAvatarSelected,
        onError = onAvatarError
    )

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraImageUri?.let(onAvatarSelected)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(Modifier.weight(0.5f))
                Text(
                    text = if (isEditing) stringResource(R.string.edit_group) else stringResource(R.string.create_group),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(Modifier.weight(0.5f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.Center) {
                AvatarPicker(
                    avatarUrl = effectiveAvatarUrl,
                    onClick = { showAvatarMenu = true },
                    onLongClick = { showAvatarMenu = true },
                    modifier = Modifier.size(80.dp),
                    defaultIconSize = 44.dp,
                    defaultIcon = CoreR.drawable.groups
                )

                DropdownMenu(
                    expanded = showAvatarMenu,
                    onDismissRequest = { showAvatarMenu = false },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.change_avatar),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.pick_from_gallery)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CoreR.drawable.gallery),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showAvatarMenu = false
                            galleryLauncher()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.take_photo)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CoreR.drawable.camera),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showAvatarMenu = false
                            val file = File(context.cacheDir, "images/avatar_${System.currentTimeMillis()}.jpg")
                                .also { it.parentFile?.mkdirs() }
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                            cameraImageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    )

                    if (effectiveAvatarUrl != null) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.delete_photo),
                                    color = Color(0xFFE53935)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CoreR.drawable.delete),
                                    contentDescription = null,
                                    tint = Color(0xFFE53935)
                                )
                            },
                            onClick = {
                                showAvatarMenu = false
                                onAvatarDeleted()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.group_name),
                    style = MaterialTheme.typography.headlineMedium
                )
                CompactOutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = stringResource(R.string.group_name_hint),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.group_description),
                    style = MaterialTheme.typography.headlineMedium
                )
                CompactOutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = stringResource(R.string.group_description_hint),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            EmailsSection(
                emails = emails,
                onAddEmail = onAddEmail,
                onEmailChange = onEmailChange,
                onRemoveEmail = onRemoveEmail
            )

            Spacer(modifier = Modifier.height(20.dp))

            DefaultButton(
                text = if (isEditing) stringResource(R.string.save_group_button) else stringResource(R.string.create_group_button),
                onClick = onSubmit,
                isButtonEnabled = !isLoading,
                isWidthLimited = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
