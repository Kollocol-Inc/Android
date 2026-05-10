package com.ziopam.kollocol.feature.main.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.input.Switch
import com.ziopam.kollocol.domain.model.NotificationSettings
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

internal val deadlineOptions = listOf(
    "never" to R.string.deadline_never,
    "1h" to R.string.deadline_1h,
    "3h" to R.string.deadline_3h,
    "6h" to R.string.deadline_6h,
    "12h" to R.string.deadline_12h,
    "24h" to R.string.deadline_24h
)

@Composable
internal fun deadlineLabelFor(value: String): String = when (value) {
    "1h" -> stringResource(R.string.deadline_1h)
    "3h" -> stringResource(R.string.deadline_3h)
    "6h" -> stringResource(R.string.deadline_6h)
    "12h" -> stringResource(R.string.deadline_12h)
    "24h" -> stringResource(R.string.deadline_24h)
    else -> stringResource(R.string.deadline_never)
}

@Composable
internal fun ProfileNotificationsSection(
    settings: NotificationSettings?,
    showDeadlineMenu: Boolean,
    onDeadlineMenuDismiss: () -> Unit,
    onDeadlineMenuOpen: () -> Unit,
    onNewQuizzesToggle: (Boolean) -> Unit,
    onQuizResultsToggle: (Boolean) -> Unit,
    onGroupInvitesToggle: (Boolean) -> Unit,
    onDeadlineReminderChange: (String) -> Unit
) {
    val rowStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.new_quiz_notification), style = rowStyle)
            Switch(
                isChecked = settings?.newQuizzes ?: true,
                onCheckedChange = onNewQuizzesToggle,
                modifier = Modifier.padding(vertical = 0.dp)
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.quiz_results_notification), style = rowStyle)
            Switch(
                isChecked = settings?.quizResults ?: true,
                onCheckedChange = onQuizResultsToggle,
                modifier = Modifier.padding(vertical = 0.dp)
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.group_invite_notification), style = rowStyle)
            Switch(
                isChecked = settings?.groupInvites ?: true,
                onCheckedChange = onGroupInvitesToggle,
                modifier = Modifier.padding(vertical = 0.dp)
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.deadline_reminder), style = rowStyle)

            Box {
                Row(
                    modifier = Modifier.clickableNoIndication { onDeadlineMenuOpen() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = deadlineLabelFor(settings?.deadlineReminder ?: "never"),
                        style = rowStyle,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(CoreR.drawable.expand_more),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showDeadlineMenu,
                    onDismissRequest = onDeadlineMenuDismiss,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    deadlineOptions.forEach { (value, labelRes) ->
                        DropdownMenuItem(
                            text = { Text(stringResource(labelRes)) },
                            onClick = {
                                onDeadlineReminderChange(value)
                                onDeadlineMenuDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}
