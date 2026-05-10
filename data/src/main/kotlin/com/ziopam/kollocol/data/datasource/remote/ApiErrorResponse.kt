package com.ziopam.kollocol.data.datasource.remote

import com.google.gson.annotations.SerializedName

data class ApiErrorResponse(
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("message")
    val message: String? = null
)