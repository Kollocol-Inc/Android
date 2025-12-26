package com.ziopam.kollocol.feature.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ziopam.kollocol.navigation.Graph
import com.ziopam.kollocol.navigation.graphViewModel

object AuthRoute {
    const val ENTER_EMAIL = "enter_email"
    const val ENTER_CODE = "enter_code"
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    navigation(
        route = Graph.AUTH,
        startDestination = AuthRoute.ENTER_EMAIL
    ) {
        composable(AuthRoute.ENTER_EMAIL) { backStackEntry ->
            val vm = graphViewModel<AuthViewModel>(
                navBackStackEntry=backStackEntry, navController=navController, graphRoute=Graph.AUTH
            )
            EmailScreen(vm) { navController.navigate(AuthRoute.ENTER_CODE) }
        }

        composable(AuthRoute.ENTER_CODE) { backStackEntry ->
            val vm = graphViewModel<AuthViewModel>(
                navBackStackEntry=backStackEntry, navController=navController, graphRoute=Graph.AUTH
            )
            CodeScreen(vm)
        }
    }
}