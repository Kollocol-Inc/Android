package com.ziopam.kollocol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

const val MAX_BUTTON_WIDTH = 320
const val MAX_EDITTEXT_WIDTH = 488

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val materialColors = if (darkTheme) DarkMaterialScheme else LightMaterialScheme

    MaterialTheme(
        colorScheme = materialColors,
        typography = Typography,
    ) {
        content()
    }
}