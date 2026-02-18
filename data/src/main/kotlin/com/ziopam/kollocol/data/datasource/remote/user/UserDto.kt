package com.ziopam.kollocol.data.datasource.remote.user

import com.google.gson.annotations.SerializedName
import com.ziopam.kollocol.domain.model.User

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

data class GetUserResponseDto(
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null
) {
    fun toUser(): User {
        return User(
            avatarUrl = avatarUrl,
            firstName = firstName.orEmpty(),
            lastName = lastName.orEmpty()
        )
    }
}

data class UpdateUserRequestDto(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)