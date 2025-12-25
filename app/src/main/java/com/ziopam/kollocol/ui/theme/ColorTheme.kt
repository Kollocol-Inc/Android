package com.ziopam.kollocol.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightMaterialScheme = lightColorScheme(
    primary = Color(0xFF5B5CE2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE4E5FF),

    secondary = Color(0xFF8B8FE8),
    secondaryContainer = Color(0xFFF0F1FF),

    background = Color(0xFFF4F5F7),
    onBackground = Color(0xFF1B1C1F),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1C1F),
    surfaceVariant = Color(0xFFECEFF3),
    onSurfaceVariant = Color(0xFF5A5D66),

    outline = Color(0xFFD8DCE2),
)

val DarkMaterialScheme = darkColorScheme(
    primary = Color(0xFF37379F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF2F305E),

    secondary = Color(0xFFA2A5FF),
    secondaryContainer = Color(0xFF24254A),

    background = Color(0xFF0F1015),
    onBackground = Color(0xFFEDEDF2),

    surface = Color(0xFF1A1B22),
    onSurface = Color(0xFFEDEDF2),
    surfaceVariant = Color(0xFF2A2B35),
    onSurfaceVariant = Color(0xFFB7B8C4),

    outline = Color(0xFF3A3B45),
)
