package com.ziopam.kollocol.core.common

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeFormatter {

    private val deadlineFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")

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

    fun formatDeadline(raw: String?): String? {
        if (raw == null) return null
        return try {
            val zdt = try {
                ZonedDateTime.parse(raw)
            } catch (_: Exception) {
                LocalDateTime.parse(raw).atZone(ZoneOffset.UTC)
            }
            zdt.format(deadlineFormatter)
        } catch (_: Exception) {
            raw
        }
    }
}
