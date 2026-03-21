package com.ziopam.kollocol.core.common

object TimeFormatter {
    fun formatTime(totalSeconds: Int): String {
        val t = totalSeconds.coerceAtLeast(0)

        val h = t / 3600
        val m = (t % 3600) / 60
        val s = t % 60

        val parts = mutableListOf<String>()

        if (h > 0) parts += "$h ч"
        if (m > 0) parts += "$m м"

        val showSeconds = s > 0 || (h == 0 && m == 0)
        if (showSeconds) parts += "$s с"

        return parts.joinToString(" ")
    }
}