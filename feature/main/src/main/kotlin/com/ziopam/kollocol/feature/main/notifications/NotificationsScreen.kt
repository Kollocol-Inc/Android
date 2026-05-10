package com.ziopam.kollocol.feature.main.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.domain.model.Notification
import com.ziopam.kollocol.domain.model.NotificationType
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.notifications.components.NotificationItem
import kotlinx.coroutines.flow.distinctUntilChanged
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationsScreen(
        notifications = state.notifications,
        onNavigateBack = onNavigateBack,
        onMarkAllRead = viewModel::markAllAsRead,
        onVisibleNotifications = viewModel::onVisibleNotifications,
        onAcceptInvite = viewModel::acceptGroupInvite,
        onDeclineInvite = viewModel::declineGroupInvite
    )
}

@Composable
fun NotificationsScreen(
    notifications: List<Notification>,
    onNavigateBack: () -> Unit,
    onMarkAllRead: () -> Unit,
    onVisibleNotifications: (List<String>) -> Unit,
    onAcceptInvite: (groupId: String, notificationId: String) -> Unit,
    onDeclineInvite: (groupId: String, notificationId: String) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .distinctUntilChanged()
            .collect { visibleItems ->
                val ids = visibleItems.mapNotNull { it.key as? String }
                if (ids.isNotEmpty()) onVisibleNotifications(ids)
            }
    }

    LayoutWithLargeBottomCard(
        scrollable = false,
        contentAbove = {
            NotificationsHeader(
                onNavigateBack = onNavigateBack,
                onMarkAllRead = onMarkAllRead
            )
        }
    ) {
        if (notifications.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.no_notifications),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                var number = 1
                items(
                    items = notifications,
                    key = { it.id }
                ) { notification ->
                    NotificationItem(
                        notification = notification,
                        onAcceptInvite = { groupId -> onAcceptInvite(groupId, notification.id) },
                        onDeclineInvite = { groupId -> onDeclineInvite(groupId, notification.id) }
                    )
                    if (number < notifications.size){
                        HorizontalDivider()
                    }
                    ++number
                }
            }
        }
    }
}

@Composable
private fun NotificationsHeader(
    onNavigateBack: () -> Unit,
    onMarkAllRead: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        CircleIconButton(
            onClick = onNavigateBack,
            icon = ImageVector.vectorResource(CoreR.drawable.arrow_back),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Text(
            text = stringResource(R.string.notifications_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Center)
        )

        CircleIconButton(
            onClick = onMarkAllRead,
            icon = ImageVector.vectorResource(CoreR.drawable.done_all),
            contentDescription = stringResource(R.string.mark_all_read),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

private val previewNotifications = listOf(
    Notification(
        id = "1",
        userId = "u1",
        type = NotificationType.GROUP_INVITE,
        title = "Приглашение в группу",
        content = "Вы были приглашены в группу Amogus",
        isRead = false,
        createdAt = "2026-05-08T10:00:00Z",
        relatedEntityId = "group-1"
    ),
    Notification(
        id = "2",
        userId = "u1",
        type = NotificationType.QUIZ_CREATED,
        title = "Новый квиз",
        content = "Доступен новый квиз «Коллоквиум Android»",
        isRead = false,
        createdAt = "2026-05-08T09:00:00Z"
    ),
    Notification(
        id = "3",
        userId = "u1",
        type = NotificationType.QUIZ_RESULTS,
        title = "Результаты квиза · Коллоквиум Android",
        content = "Оценка: 100% · 10/10 б.",
        isRead = true,
        createdAt = "2026-05-07T18:00:00Z"
    ),
    Notification(
        id = "4",
        userId = "u1",
        type = NotificationType.GRADE_CHANGED,
        title = "Оценка изменена",
        content = "Преподаватель изменил вашу оценку на 8/10 б.",
        isRead = true,
        createdAt = "2026-05-07T15:00:00Z"
    ),
    Notification(
        id = "5",
        userId = "u1",
        type = NotificationType.DEADLINE_REMINDER,
        title = "Дедлайн · Коллоквиум Android",
        content = "Дедлайн квиза \"Коллоквиум Android\" наступит через 5 часов, поспеши!",
        isRead = false,
        createdAt = "2026-05-07T12:00:00Z"
    )
)

@PreviewLightDark
@Composable
private fun NotificationsScreenWithItemsPreview() {
    AppPreview {
        NotificationsScreen(
            notifications = previewNotifications,
            onNavigateBack = {},
            onMarkAllRead = {},
            onVisibleNotifications = {},
            onAcceptInvite = { _, _ -> },
            onDeclineInvite = { _, _ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun NotificationsScreenEmptyPreview() {
    AppPreview {
        NotificationsScreen(
            notifications = emptyList(),
            onNavigateBack = {},
            onMarkAllRead = {},
            onVisibleNotifications = {},
            onAcceptInvite = { _, _ -> },
            onDeclineInvite = { _, _ -> }
        )
    }
}
