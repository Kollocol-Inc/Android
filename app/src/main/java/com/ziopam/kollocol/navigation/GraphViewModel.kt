package com.ziopam.kollocol.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@Composable
inline fun <reified VM : ViewModel> graphViewModel(
    navBackStackEntry: NavBackStackEntry,
    navController: NavHostController,
    graphRoute: String
): VM {
    val parentEntry = remember(navBackStackEntry) {
        navController.getBackStackEntry(graphRoute)
    }
    return hiltViewModel(parentEntry)
}