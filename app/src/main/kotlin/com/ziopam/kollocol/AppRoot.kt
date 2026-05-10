package com.ziopam.kollocol

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ziopam.kollocol.core.navigation.Graph
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.ThemeMode
import com.ziopam.kollocol.domain.repository.SessionState
import com.ziopam.kollocol.feature.auth.authGraph
import com.ziopam.kollocol.feature.main.mainGraph

@Composable
fun AppRoot(
    startDestination: String
) {
    val navController = rememberNavController()
    val vm: AppViewModel = hiltViewModel()
    val themeMode by vm.themeMode.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDark
    }

    LaunchedEffect(Unit) {
        vm.sessionRepository.sessionState.collect { state ->
            if (state is SessionState.Unauthorized) {
                navController.navigate(Graph.AUTH) {
                    popUpTo(Graph.ROOT) {
                        inclusive = false
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }
        }
    }

    AppTheme(darkTheme = darkTheme) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            route = Graph.ROOT
        ) {
            authGraph(navController)
            mainGraph()
        }
    }
}
