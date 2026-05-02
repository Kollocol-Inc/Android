package com.ziopam.kollocol.core.ui.util

fun Int.rankToMedalEmoji(): String? = when (this) {
    1 -> "\uD83E\uDD47"
    2 -> "\uD83E\uDD48"
    3 -> "\uD83E\uDD49"
    else -> null
}
