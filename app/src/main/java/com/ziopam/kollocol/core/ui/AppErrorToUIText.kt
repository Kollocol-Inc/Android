package com.ziopam.kollocol.core.ui

import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.common.AppError

fun AppError.toUiText(): UiText = when (this) {
    is AppError.NoInternet -> UiText.StringRes(R.string.error_no_internet)
    is AppError.Timeout -> UiText.StringRes(R.string.error_timeout)
    is AppError.BadRequest -> UiText.StringRes(R.string.error_bad_request)
    is AppError.Unauthorized -> UiText.StringRes(R.string.error_unauthorized)
    is AppError.Forbidden -> UiText.StringRes(R.string.error_forbidden)
    is AppError.NotFound -> UiText.StringRes(R.string.error_not_found)
    is AppError.TooManyRequests -> UiText.StringRes(R.string.error_too_many_requests)
    is AppError.ServerError -> UiText.StringRes(R.string.error_server)
    is AppError.Unknown -> UiText.StringRes(R.string.error_unknown)
}