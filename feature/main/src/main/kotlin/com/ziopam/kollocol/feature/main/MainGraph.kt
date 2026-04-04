package com.ziopam.kollocol.feature.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ziopam.kollocol.core.navigation.Graph

object MainRoute {
    const val HOME = "home"
    const val GROUPS = "groups"
    const val QUIZZES = "quizzes"
    const val PROFILE = "profile"
    const val CREATE_TEMPLATE = "create_template"
    const val GAME = "game/{accessCode}"

    fun gameRoute(accessCode: String) = "game/$accessCode"
}

fun NavGraphBuilder.mainGraph() {
    composable(Graph.MAIN) {
        MainRootScreen()
    }
}
