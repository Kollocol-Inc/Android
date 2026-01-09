package com.ziopam.kollocol.core.common

sealed interface AppError {
    data object NoInternet : AppError
    data object Timeout : AppError
    data class Network(val message: String? = null) : AppError

    data class Http(
        val code: Int,
        val error: String? = null,
        val message: String? = null
    ) : AppError

    data object Unauthorized : AppError
    data object Forbidden : AppError

    data class Unknown(val message: String? = null) : AppError
}
