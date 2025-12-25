package com.ziopam.kollocol.feature.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ziopam.kollocol.navigation.AuthRoute
import com.ziopam.kollocol.navigation.Graph

fun NavGraphBuilder.authGraph(
    onAuthorized: () -> Unit
) {
    navigation(
        route = Graph.AUTH,
        startDestination = AuthRoute.LOGIN
    ) {
        composable(AuthRoute.LOGIN) {
            LoginScreen(onLoginSuccess = onAuthorized)
        }
    }
}