package com.ziopam.kollocol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

// TODO Описать константы для максимального длины поля и максимального размера кнопки

const val MAX_BUTTON_WIDTH = 320
const val MAX_EDITTEXT_WIDTH = 488

val LocalExtraColors = staticCompositionLocalOf<ExtraColors> {
    error("ExtraColors not provided")
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkExtraColors else LightExtraColors
    val materialColors = if (darkTheme) DarkMaterialScheme else LightMaterialScheme

    MaterialTheme(
        colorScheme = materialColors,
        typography = Typography,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalExtraColors provides appColors,
            content = content
        )
    }
}