package com.ziopam.kollocol.data.datasource.remote.auth

import com.ziopam.kollocol.core.network.model.RefreshTokenRequestDto
import com.ziopam.kollocol.core.network.model.RefreshTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto)

    @POST("auth/resend-code")
    suspend fun resendCode(@Body request: LoginRequestDto)

    @POST("auth/verify")
    suspend fun verify(@Body request: VerifyCodeRequestDto): VerifyCodeResponseDto

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequestDto): RefreshTokenResponseDto

    @POST("auth/logout")
    suspend fun logout(): LogoutResponseDto
}
