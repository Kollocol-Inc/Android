package com.ziopam.kollocol.feature.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ziopam.kollocol.core.ui.cards.LocalExtraBottomPadding
import com.ziopam.kollocol.feature.main.home.HomeScreen
import com.ziopam.kollocol.feature.main.quizzes.QuizzesScreen
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.CreateTemplateScreen
import com.ziopam.kollocol.feature.quizgame.GameScreen

@Composable
fun MainRootScreen() {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute !in listOf(MainRoute.CREATE_TEMPLATE) &&
            currentRoute?.startsWith("game/") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(navController = tabNavController)
            }
        }
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalExtraBottomPadding provides paddingValues.calculateBottomPadding()
        ) {
            NavHost(
                navController = tabNavController,
                startDestination = MainRoute.HOME,
                modifier = Modifier.fillMaxSize()
            ) {
            composable(
                route = MainRoute.HOME,
                exitTransition = {
                    if (targetState.destination.route == MainRoute.GAME) {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    } else {
                        fadeOut(animationSpec = tween(200))
                    }
                },
                popEnterTransition = {
                    if (initialState.destination.route == MainRoute.GAME) {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    } else {
                        fadeIn(animationSpec = tween(200))
                    }
                }
            ) {
                HomeScreen(
                    onJoinQuiz = { code ->
                        tabNavController.navigate(MainRoute.gameRoute(code)) {
                            launchSingleTop = true
                        }
                    },
                    onParticipatingQuizClick = { quiz ->
                        tabNavController.navigate(MainRoute.gameRoute(quiz.accessCode)) {
                            launchSingleTop = true
                        }
                    },
                    onRunningQuizClick = { quiz ->
                        tabNavController.navigate(MainRoute.gameRoute(quiz.accessCode)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(MainRoute.GROUPS) {
                Text("Groups screen")
            }

            composable(
                route = MainRoute.QUIZZES,
                exitTransition = {
                    val target = targetState.destination.route
                    if (target == MainRoute.CREATE_TEMPLATE || target == MainRoute.GAME) {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    } else {
                        fadeOut(animationSpec = tween(200))
                    }
                },
                popEnterTransition = {
                    val initial = initialState.destination.route
                    if (initial == MainRoute.CREATE_TEMPLATE || initial == MainRoute.GAME) {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    } else {
                        fadeIn(animationSpec = tween(200))
                    }
                }
            ) {
                QuizzesScreen(
                    onRunningQuizClick = { quiz ->
                        tabNavController.navigate(MainRoute.gameRoute(quiz.accessCode)) {
                            launchSingleTop = true
                        }
                    },
                    onCreateTemplateClick = {
                        tabNavController.navigate(MainRoute.CREATE_TEMPLATE)
                    }
                )
            }

            composable(MainRoute.PROFILE) {
                Text("Profile screen")
            }

            composable(
                route = MainRoute.CREATE_TEMPLATE,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                }
            ) {
                CreateTemplateScreen(
                    onNavigateBack = { tabNavController.popBackStack() }
                )
            }

            composable(
                route = MainRoute.GAME,
                arguments = listOf(navArgument("accessCode") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(300)
                    )
                }
            ) {
                GameScreen(
                    onNavigateBack = { tabNavController.popBackStack() }
                )
            }
        }
        }
    }
}