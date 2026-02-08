package com.ziopam.kollocol.data.datasource.remote.auth

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(val email: String)

data class VerifyCodeRequestDto(
    val code: String,
    val email: String
)

data class VerifyCodeResponseDto(
    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    @SerializedName("is_registered")
    val isRegistered: Boolean? = null,
)

data class LogoutResponseDto(
    val message: String? = null,
    val success: Boolean? = null
)
