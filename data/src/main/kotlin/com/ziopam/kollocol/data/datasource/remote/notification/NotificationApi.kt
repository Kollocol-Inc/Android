package com.ziopam.kollocol.data.datasource.remote.notification

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0
    ): GetNotificationsResponseDto

    @PUT("notifications/read")
    suspend fun markAsRead(@Body request: NotificationIdsRequestDto)
}
