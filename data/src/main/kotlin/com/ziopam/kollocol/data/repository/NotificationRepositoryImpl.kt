package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.group.GroupApi
import com.ziopam.kollocol.data.datasource.remote.notification.NotificationApi
import com.ziopam.kollocol.data.datasource.remote.notification.NotificationIdsRequestDto
import com.ziopam.kollocol.domain.model.Notification
import com.ziopam.kollocol.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
    private val groupApi: GroupApi,
    private val safeApiCall: SafeApiCall
) : NotificationRepository {

    override suspend fun getNotifications(limit: Int, offset: Int): AppResult<List<Notification>> {
        val result = safeApiCall.call { notificationApi.getNotifications(limit, offset) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.notifications.map { it.toDomain() })
            is AppResult.Err -> result
        }
    }

    override suspend fun markAsRead(ids: List<String>): AppResult<Unit> {
        return safeApiCall.call {
            notificationApi.markAsRead(NotificationIdsRequestDto(ids))
        }
    }

    override suspend fun acceptGroupInvite(groupId: String): AppResult<Unit> {
        return safeApiCall.call { groupApi.acceptInvite(groupId) }
    }

    override suspend fun declineGroupInvite(groupId: String): AppResult<Unit> {
        return safeApiCall.call { groupApi.declineInvite(groupId) }
    }

    override suspend fun getUnreadCount(): AppResult<Int> {
        val result = safeApiCall.call { notificationApi.getNotifications(limit = 100, offset = 0) }
        return when (result) {
            is AppResult.Ok -> AppResult.Ok(result.value.notifications.count { !it.isRead })
            is AppResult.Err -> result
        }
    }
}
