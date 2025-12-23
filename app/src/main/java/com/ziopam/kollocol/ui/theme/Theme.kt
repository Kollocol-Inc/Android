package com.ziopam.kollocol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("AppColors not provided")
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val materialColors = if (darkTheme) DarkMaterialScheme else LightMaterialScheme

    MaterialTheme(
        colorScheme = materialColors,
        typography = Typography,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalAppColors provides appColors,
            content = content
        )
    }
}