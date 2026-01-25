package com.ziopam.kollocol.data.datasource.remote.user

import com.google.gson.annotations.SerializedName

data class RegisterUserRequestDto(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)

data class RegisterUserResponseDto(
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null
)