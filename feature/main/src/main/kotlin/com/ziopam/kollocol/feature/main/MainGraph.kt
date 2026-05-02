package com.ziopam.kollocol.feature.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ziopam.kollocol.core.navigation.Graph

object MainRoute {
    const val HOME = "home"
    const val GROUPS = "groups"
    const val QUIZZES = "quizzes"
    const val PROFILE = "profile"
    const val CREATE_TEMPLATE = "create_template?templateId={templateId}&aiPrompt={aiPrompt}"
    const val GAME = "game/{accessCode}"

    fun gameRoute(accessCode: String) = "game/$accessCode"
    fun createTemplateRoute() = "create_template"
    fun editTemplateRoute(id: String) = "create_template?templateId=$id"
    fun createTemplateAiRoute(prompt: String) = "create_template?aiPrompt=${android.net.Uri.encode(prompt)}"
}

fun NavGraphBuilder.mainGraph() {
    composable(Graph.MAIN) {
        MainRootScreen()
    }
}
