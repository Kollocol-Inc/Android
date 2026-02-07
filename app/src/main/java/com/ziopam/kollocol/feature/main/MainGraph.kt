package com.ziopam.kollocol.feature.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ziopam.kollocol.navigation.Graph

object MainRoute {
    const val HOME = "home"
    const val GROUPS = "groups"
    const val QUIZZES = "quizzes"
    const val PROFILE = "profile"
}

fun NavGraphBuilder.mainGraph() {
    composable(Graph.MAIN) {
        MainRootScreen()
    }
}
