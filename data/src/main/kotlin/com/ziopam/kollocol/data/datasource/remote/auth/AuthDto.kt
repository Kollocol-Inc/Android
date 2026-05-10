package com.ziopam.kollocol.data.datasource.remote.auth

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email")
    val email: String
)

data class VerifyCodeRequestDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("email")
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
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("success")
    val success: Boolean? = null
)
