package com.ziopam.kollocol.feature.main

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.ziopam.kollocol.core.ui.theme.AppTheme


@Preview
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
internal fun MainScaffoldPreview(
    content: @Composable () -> Unit = {}
) {
    val tabNavController = rememberNavController()

    AppTheme {
        Scaffold (
            bottomBar = {
                MainBottomBar(navController = tabNavController)
            }
        ) { _ ->
            content()
        }
    }
}