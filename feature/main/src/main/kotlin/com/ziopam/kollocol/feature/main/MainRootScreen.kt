package com.ziopam.kollocol.feature.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.feature.main.home.HomeScreen
import com.ziopam.kollocol.feature.main.quizzes.QuizzesScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainRootScreen() {
    val tabNavController = rememberNavController()

    Scaffold (
        bottomBar = {
            MainBottomBar(navController = tabNavController)
        }
    ) { _ ->
        NavHost(
            navController = tabNavController,
            startDestination = MainRoute.HOME,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(MainRoute.HOME) {
                HomeScreen()
            }

            composable(MainRoute.GROUPS) {
                Text("Groups screen")
            }

            composable(MainRoute.QUIZZES) {
                QuizzesScreen(
                    quizzesInfoExample,
                    emptyList(),
                    quizzesInfoExample,
                    "",
                    {},
                    {}
                )
            }

            composable(MainRoute.PROFILE) {
                Text("Profile screen")
            }
        }
    }
}