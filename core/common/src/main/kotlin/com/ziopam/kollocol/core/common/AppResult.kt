package com.ziopam.kollocol.core.common

sealed class AppResult<out T> {
    data class Ok<T>(val value: T) : AppResult<T>()
    data class Err(val error: AppError) : AppResult<Nothing>()
}