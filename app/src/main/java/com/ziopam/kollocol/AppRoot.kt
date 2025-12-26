package com.ziopam.kollocol

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ziopam.kollocol.feature.auth.authGraph
import com.ziopam.kollocol.feature.main.mainGraph
import com.ziopam.kollocol.navigation.Graph

@Composable
fun AppRoot(
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        route = Graph.ROOT
    ) {

        authGraph(navController)
        mainGraph()
    }
}
