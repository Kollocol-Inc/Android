package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(limit: Int = 30, offset: Int = 0): AppResult<List<Notification>>
    suspend fun markAsRead(ids: List<String>): AppResult<Unit>
    suspend fun acceptGroupInvite(groupId: String): AppResult<Unit>
    suspend fun declineGroupInvite(groupId: String): AppResult<Unit>
    suspend fun getUnreadCount(): AppResult<Int>
}
