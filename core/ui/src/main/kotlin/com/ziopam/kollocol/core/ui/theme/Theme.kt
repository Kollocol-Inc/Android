package com.ziopam.kollocol.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

const val MAX_BUTTON_WIDTH = 320
const val MAX_EDITTEXT_WIDTH = 488

val LocalIsDarkTheme = compositionLocalOf { false }

@Composable
fun isDarkTheme(): Boolean = LocalIsDarkTheme.current

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val materialColors = if (darkTheme) DarkMaterialScheme else LightMaterialScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = Typography,
        ) {
            content()
        }
    }
}