package com.ziopam.kollocol.data.datasource.remote.user

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @POST("users/register")
    suspend fun registerUser(@Body request: RegisterUserRequestDto): RegisterUserResponseDto

    @GET("users/me")
    suspend fun getUser(): GetUserResponseDto

    @POST("users/me")
    suspend fun updateUser(@Body request: UpdateUserRequestDto)
}