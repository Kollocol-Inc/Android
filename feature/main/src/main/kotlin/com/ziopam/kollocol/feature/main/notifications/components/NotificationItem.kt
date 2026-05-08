package com.ziopam.kollocol.feature.main.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.domain.model.Notification
import com.ziopam.kollocol.domain.model.NotificationType
import com.ziopam.kollocol.feature.main.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
fun NotificationItem(
    notification: Notification,
    onAcceptInvite: (groupId: String) -> Unit,
    onDeclineInvite: (groupId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(notification.createdAt) {
        runCatching {
            val instant = Instant.parse(notification.createdAt)
            val formatter = DateTimeFormatter.ofPattern("dd.MM").withZone(ZoneId.systemDefault())
            formatter.format(instant)
        }.getOrElse { "" }
    }

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NotificationIcon(type = notification.type)

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = notification.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                val groupId = notification.referenceId
                if (notification.type == NotificationType.GROUP_INVITE && groupId != null) {
                    Spacer(Modifier.height(10.dp))
                    GroupInviteActions(
                        onAccept = { onAcceptInvite(groupId) },
                        onDecline = { onDeclineInvite(groupId) }
                    )
                }
            }
        }

        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(10.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Composable
private fun NotificationIcon(type: NotificationType) {
    val iconRes = when (type) {
        NotificationType.GROUP_INVITE -> CoreR.drawable.groups
        NotificationType.QUIZ_CREATED -> CoreR.drawable.play
        NotificationType.QUIZ_RESULTS -> CoreR.drawable.question_in_circle_filled
        NotificationType.GRADE_CHANGED -> CoreR.drawable.check
        NotificationType.DEADLINE_REMINDER -> CoreR.drawable.clock_filled
        NotificationType.UNKNOWN -> CoreR.drawable.bell
    }

    val iconTint = when (type) {
        NotificationType.GROUP_INVITE -> MaterialTheme.colorScheme.primary
        NotificationType.QUIZ_CREATED -> Color(0xFF4CAF50)
        NotificationType.QUIZ_RESULTS -> MaterialTheme.colorScheme.primary
        NotificationType.GRADE_CHANGED -> ExtraColors.affirmative
        NotificationType.DEADLINE_REMINDER -> Color(0xFFE53935)
        NotificationType.UNKNOWN -> MaterialTheme.colorScheme.primary
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .background(
                color = iconTint.copy(alpha = 0.12f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun GroupInviteActions(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onAccept,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.accept_invite))
        }
        OutlinedButton(
            onClick = onDecline,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.decline_invite))
        }
    }
}
