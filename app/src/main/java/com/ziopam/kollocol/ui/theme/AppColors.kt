package com.ziopam.kollocol.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,

    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val secondaryContainer: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textOnPrimary: Color,

    val iconPrimary: Color,
    val iconSecondary: Color,

    val buttonDisabled: Color,
    val buttonDisabledText: Color,
)
