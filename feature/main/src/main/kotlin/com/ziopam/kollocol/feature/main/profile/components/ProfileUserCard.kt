package com.ziopam.kollocol.feature.main.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.theme.isDarkTheme
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun ProfileUserCard(
    firstName: String,
    lastName: String,
    email: String,
    avatarUrl: String?,
    showMenu: Boolean,
    onMenuDismiss: () -> Unit,
    onCardLongClick: () -> Unit,
    onEditName: () -> Unit,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onDeletePhoto: () -> Unit
) {
    val cardColor = if (isDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AvatarPicker(
                    avatarUrl = avatarUrl,
                    onClick = onCardLongClick,
                    onLongClick = onCardLongClick,
                    modifier = Modifier.size(64.dp),
                    defaultIconSize = 32.dp
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onMenuDismiss,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit_name_title)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CoreR.drawable.edit),
                                contentDescription = null
                            )
                        },
                        onClick = onEditName
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

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
                        onClick = onPickFromGallery
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.take_photo)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CoreR.drawable.camera),
                                contentDescription = null
                            )
                        },
                        onClick = onTakePhoto
                    )

                    if (avatarUrl != null) {
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
                            onClick = onDeletePhoto
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "$firstName $lastName",
                    style = MaterialTheme.typography.headlineSmall
                )
                if (email.isNotEmpty()) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
