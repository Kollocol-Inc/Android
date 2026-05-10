package com.ziopam.kollocol.feature.main.groups.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun GroupDetailAbove(
    groupName: String,
    description: String,
    avatarUrl: String?,
    isOwner: Boolean,
    showMenu: Boolean,
    onBackClick: () -> Unit,
    onMenuToggle: () -> Unit,
    onMenuDismiss: () -> Unit,
    onLeaveClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        CircleIconButton(
            onClick = onBackClick,
            icon = ImageVector.vectorResource(CoreR.drawable.arrow_back),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = groupName,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            AvatarPicker(
                avatarUrl = avatarUrl,
                onClick = onMenuToggle,
                onLongClick = onMenuToggle,
                modifier = Modifier.size(48.dp),
                defaultIconSize = 26.dp,
                defaultIcon = CoreR.drawable.groups
            )

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = onMenuDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isOwner) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit_group)) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CoreR.drawable.edit),
                                contentDescription = null
                            )
                        },
                        onClick = onEditClick
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.delete_group),
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
                        onClick = onDeleteClick
                    )
                } else {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.leave_group),
                                color = Color(0xFFE53935)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CoreR.drawable.exit),
                                contentDescription = null,
                                tint = Color(0xFFE53935)
                            )
                        },
                        onClick = onLeaveClick
                    )
                }
            }
        }
    }
}
