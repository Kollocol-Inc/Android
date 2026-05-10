package com.ziopam.kollocol.domain.model

data class User(
    val avatarUrl: String?,
    val firstName: String,
    val lastName: String,
    val email: String = ""
)
