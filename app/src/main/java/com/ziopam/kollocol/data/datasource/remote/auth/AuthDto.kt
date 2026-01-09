package com.ziopam.kollocol.data.datasource.remote.auth

data class LoginRequestDto(val email: String)

data class VerifyCodeRequestDto(
    val code: String,
    val email: String
)

// TODO Подумать об удалении лишних полей
data class VerifyCodeResponseDto(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val isRegistered: Boolean? = null,
    val success: Boolean? = null,
    val message: String? = null,
    val userId: String? = null
)

data class RefreshTokenRequestDto(val refreshToken: String)

data class RefreshTokenResponseDto(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val success: Boolean? = null,
    val message: String? = null
)

data class LogoutResponseDto(
    val message: String? = null,
    val success: Boolean? = null
)
