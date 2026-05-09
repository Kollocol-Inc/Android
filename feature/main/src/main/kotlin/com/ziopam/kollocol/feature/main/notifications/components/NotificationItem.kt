package com.ziopam.kollocol.feature.main.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
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

    Column(modifier = modifier.padding(12.dp).padding(vertical = 5.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            NotificationIcon(type = notification.type)

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!notification.isRead){
                        Box(Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(Modifier.width(5.dp))
                    }

                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = notification.content,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
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

@Composable
private fun NotificationIcon(type: NotificationType) {
    val iconRes = when (type) {
        NotificationType.GROUP_INVITE -> CoreR.drawable.groups_filled
        NotificationType.QUIZ_CREATED -> CoreR.drawable.play
        NotificationType.QUIZ_RESULTS,NotificationType.GRADE_CHANGED   -> CoreR.drawable.question_in_circle_filled
        NotificationType.DEADLINE_REMINDER -> CoreR.drawable.clock_filled
        NotificationType.UNKNOWN -> CoreR.drawable.bell
    }

    val iconTint = when (type) {
        NotificationType.GROUP_INVITE -> MaterialTheme.colorScheme.primary
        NotificationType.QUIZ_CREATED -> Color(0xFF4CAF50)
        NotificationType.QUIZ_RESULTS, NotificationType.GRADE_CHANGED   -> MaterialTheme.colorScheme.primary
        NotificationType.DEADLINE_REMINDER -> Color(0xFFE53935)
        NotificationType.UNKNOWN -> MaterialTheme.colorScheme.primary
    }

    Box(
        contentAlignment = Alignment.Center,
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
        Button(
            onClick = onDecline,
            modifier = Modifier.weight(1f),
            colors = ButtonColors(
                containerColor = Color(0xFF6D6B6B),
                disabledContainerColor = Color(0xFF6D6B6B),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(stringResource(R.string.decline_invite))
        }
    }
}
