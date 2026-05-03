package com.ziopam.kollocol.data.datasource.remote.user

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApi {
    @POST("users/register")
    suspend fun registerUser(@Body request: RegisterUserRequestDto): RegisterUserResponseDto

    @GET("users/me")
    suspend fun getUser(): GetUserResponseDto

    @PUT("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): GetUserResponseDto

    @DELETE("users/me")
    suspend fun deleteAccount()

    @Multipart
    @POST("users/me/avatar/upload")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): GetUserResponseDto

    @DELETE("users/me/avatar/delete")
    suspend fun deleteAvatar()

    @GET("users/me/notifications")
    suspend fun getNotificationSettings(): NotificationSettingsDto

    @PUT("users/me/notifications")
    suspend fun updateNotificationSettings(@Body request: UpdateNotificationSettingsRequestDto): NotificationSettingsDto
}
