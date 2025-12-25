package com.ziopam.kollocol.feature.main

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ziopam.kollocol.navigation.Graph
import com.ziopam.kollocol.ui.AppScaffold

object MainRoute { const val HOME = "home" }

fun NavGraphBuilder.mainGraph() {
    navigation(
        route = Graph.MAIN,
        startDestination = MainRoute.HOME
    ) {
        composable(MainRoute.HOME) {
            AppScaffold {
                Text("Hello World")
            }
        }
    }
}
