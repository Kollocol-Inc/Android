package com.ziopam.kollocol.core.common

sealed interface AppError {
    data object NoInternet : AppError
    data object Timeout : AppError

    data class BadRequest(val message: String?) : AppError
    data object Unauthorized : AppError
    data object Forbidden : AppError
    data object NotFound : AppError
    data object TooManyRequests : AppError
    data object ServerError : AppError

    data class Unknown(val message: String? = null) : AppError
}