package com.ziopam.kollocol.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightMaterialScheme = lightColorScheme(
    primary = Color(0xFF534DC9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFECECEC),
    onPrimaryContainer = Color(0xFF171717),

    secondary = Color(0xFF8B8FE8),
    secondaryContainer = Color(0xFFF0F1FF),

    background = Color(0xFFF4F5F7),
    onBackground = Color(0xFF1B1C1F),

    surface = Color(0xFFF7F4FB),
    onSurface = Color(0xFF1B1C1F),
    surfaceVariant = Color(0xFFEAEAEA),
    onSurfaceVariant = Color(0xFF5A5D66),

    outline = Color(0xFFD8DCE2),
)

val DarkMaterialScheme = darkColorScheme(
    primary = Color(0xFF534DC9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF2F305E),
    onPrimaryContainer = Color(0xFFD3C8E9),

    secondary = Color(0xFFA2A5FF),
    secondaryContainer = Color(0xFF24254A),

    background = Color(0xFF1F202C),
    onBackground = Color(0xFFEDEDF2),

    surface = Color(0xFF2A2B38),
    onSurface = Color(0xFFEDEDF2),
    surfaceVariant = Color(0xFF353542),
    onSurfaceVariant = Color(0xFFB7B8C4),

    outline = Color(0xE63A3B45),
)
