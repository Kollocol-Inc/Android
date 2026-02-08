package com.ziopam.kollocol.data.datasource.remote.user

import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("users/register")
    suspend fun registerUser(@Body request: RegisterUserRequestDto): RegisterUserResponseDto
}