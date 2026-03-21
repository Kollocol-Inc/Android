package com.ziopam.kollocol.core.network.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequestDto(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class RefreshTokenResponseDto(
    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("refresh_token")
    val refreshToken: String? = null
)