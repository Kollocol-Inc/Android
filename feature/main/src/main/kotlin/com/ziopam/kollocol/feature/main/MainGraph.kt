package com.ziopam.kollocol.feature.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ziopam.kollocol.core.navigation.Graph

object MainRoute {
    const val HOME = "home"
    const val GROUPS = "groups"
    const val GROUP_DETAIL = "group_detail/{groupId}"
    const val QUIZZES = "quizzes"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"
    const val CREATE_TEMPLATE = "create_template?templateId={templateId}&aiPrompt={aiPrompt}"
    const val GAME = "game/{accessCode}"
    const val QUIZ_REVIEW = "quiz_review/{instanceId}"
    const val PARTICIPANT_REVIEW = "participant_review/{instanceId}/{userId}?name={name}&email={email}"

    fun gameRoute(accessCode: String) = "game/$accessCode"
    fun createTemplateRoute() = "create_template"
    fun editTemplateRoute(id: String) = "create_template?templateId=$id"
    fun createTemplateAiRoute(prompt: String) = "create_template?aiPrompt=${android.net.Uri.encode(prompt)}"
    fun quizReviewRoute(instanceId: String) = "quiz_review/$instanceId"
    fun participantReviewRoute(instanceId: String, userId: String, name: String, email: String) =
        "participant_review/$instanceId/$userId?name=${android.net.Uri.encode(name)}&email=${android.net.Uri.encode(email)}"
    fun groupDetailRoute(groupId: String) = "group_detail/$groupId"
}

fun NavGraphBuilder.mainGraph() {
    composable(Graph.MAIN) {
        MainRootScreen()
    }
}
