package com.ziopam.kollocol.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// TODO Подумать об удалении лишних цветов
@Immutable
data class ExtraColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textOnPrimary: Color,

    val iconSecondary: Color,

    val buttonDisabled: Color,
    val buttonDisabledText: Color,
)

val LightExtraColors = ExtraColors(
    textPrimary = Color(0xFF1B1C1F),
    textSecondary = Color(0xFF5A5D66),
    textTertiary = Color(0xFF8B8F99),
    textOnPrimary = Color(0xFFFFFFFF),

    iconSecondary = Color(0xFF8B8F99),

    buttonDisabled = Color(0xFFC7C9D3),
    buttonDisabledText = Color(0xFFFFFFFF),
)

val DarkExtraColors = ExtraColors(
    textPrimary = Color(0xFFEDEDF2),
    textSecondary = Color(0xFFB7B8C4),
    textTertiary = Color(0xFF8A8B98),
    textOnPrimary = Color(0xFF0F1015),

    iconSecondary = Color(0xFF8A8B98),

    buttonDisabled = Color(0xFF3A3B45),
    buttonDisabledText = Color(0xFF8A8B98),
)