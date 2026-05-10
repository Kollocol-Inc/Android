package com.ziopam.kollocol.core.ui.uiText

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class StringRes(
        val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText()

    data class Dynamic(val value: String) : UiText()
}

@Composable
fun UiText.asString(): String =
    when (this) {
        is UiText.StringRes -> stringResource(resId, *args.toTypedArray())
        is UiText.Dynamic -> value
    }

fun UiText.asString(context: Context): String =
    when (this) {
        is UiText.StringRes -> context.getString(resId, *args.toTypedArray())
        is UiText.Dynamic -> value
    }
