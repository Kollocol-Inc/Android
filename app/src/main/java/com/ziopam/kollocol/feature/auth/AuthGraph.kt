package com.ziopam.kollocol.feature.auth

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ziopam.kollocol.feature.auth.code.CodeScreen
import com.ziopam.kollocol.feature.auth.email.EmailScreen
import com.ziopam.kollocol.feature.auth.personal.PersonalScreen
import com.ziopam.kollocol.feature.auth.personal.PersonalViewModel
import com.ziopam.kollocol.navigation.Graph
import com.ziopam.kollocol.navigation.graphViewModel
import kotlinx.coroutines.flow.collectLatest

object AuthRoute {
    const val ENTER_EMAIL = "enter_email"
    const val ENTER_CODE = "enter_code"
    const val ENTER_PERSONAL = "enter_personal"
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

            LaunchedEffect(backStackEntry) {
                vm.clearError()
            }

            LaunchedEffect(vm) {
                vm.events.collectLatest { event ->
                    if (event is AuthUiEvent.NavigateToCode) {
                        navController.navigate(AuthRoute.ENTER_CODE)
                    }
                }
            }

            EmailScreen(vm)
        }

        composable(AuthRoute.ENTER_CODE) { backStackEntry ->
            val vm = graphViewModel<AuthViewModel>(
                navBackStackEntry=backStackEntry, navController=navController, graphRoute=Graph.AUTH
            )

            LaunchedEffect(vm) {
                vm.events.collectLatest { event ->
                    if (event is AuthUiEvent.NavigateToPersonal) {
                        navController.navigate(AuthRoute.ENTER_PERSONAL) {
                            popUpTo(AuthRoute.ENTER_EMAIL) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }

            CodeScreen(vm)
        }

        composable(AuthRoute.ENTER_PERSONAL) { backStackEntry ->
            val vm: PersonalViewModel = hiltViewModel()
            PersonalScreen(vm) {
                navController.navigate(Graph.MAIN) {
                    popUpTo(Graph.AUTH) { inclusive = true }
                }
            }
        }
    }
}