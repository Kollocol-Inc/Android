package com.ziopam.kollocol.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

val mainScreenPadding: Dp = 16.dp

@Composable
fun MainRootScreen() {
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            MainBottomBar(navController = tabNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = MainRoute.HOME,
            modifier = Modifier.padding(innerPadding).padding(mainScreenPadding)
        ) {
            composable(MainRoute.HOME) {
                Text("Home screen")
            }

            composable(MainRoute.GROUPS) {
                Text("Groups screen")
            }

            composable(MainRoute.QUIZZES) {
                Text("Quizzes screen")
            }

            composable(MainRoute.PROFILE) {
                Text("Profile screen")
            }
        }
    }
}