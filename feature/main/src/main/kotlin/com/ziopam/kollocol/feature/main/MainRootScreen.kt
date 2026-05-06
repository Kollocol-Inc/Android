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
import com.ziopam.kollocol.domain.model.QuizMode
import com.ziopam.kollocol.feature.main.home.HomeScreen
import com.ziopam.kollocol.feature.main.profile.ProfileScreen
import com.ziopam.kollocol.feature.main.quizzes.QuizzesScreen
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.CreateTemplateScreen
import com.ziopam.kollocol.feature.main.quizzes.review.ParticipantReviewScreen
import com.ziopam.kollocol.feature.main.quizzes.review.QuizReviewScreen
import com.ziopam.kollocol.feature.quizgame.GameScreen

@Composable
fun MainRootScreen() {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute?.startsWith("create_template") != true &&
            currentRoute?.startsWith("game/") != true &&
            currentRoute?.startsWith("quiz_review/") != true &&
            currentRoute?.startsWith("participant_review/") != true

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
                        if (quiz.mode == QuizMode.ASYNC) {
                            tabNavController.navigate(MainRoute.quizReviewRoute(quiz.id))
                        } else {
                            tabNavController.navigate(MainRoute.gameRoute(quiz.accessCode)) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(MainRoute.GROUPS) {
                Text("Groups screen")
            }

            composable(
                route = MainRoute.QUIZ_REVIEW,
                arguments = listOf(navArgument("instanceId") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                }
            ) {
                QuizReviewScreen(
                    onNavigateBack = { tabNavController.popBackStack() },
                    onParticipantClick = { instanceId, participant ->
                        tabNavController.navigate(
                            MainRoute.participantReviewRoute(
                                instanceId = instanceId,
                                userId = participant.userId,
                                name = "${participant.firstName} ${participant.lastName}",
                                email = participant.email
                            )
                        )
                    }
                )
            }

            composable(
                route = MainRoute.PARTICIPANT_REVIEW,
                arguments = listOf(
                    navArgument("instanceId") { type = NavType.StringType },
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType; defaultValue = "" },
                    navArgument("email") { type = NavType.StringType; defaultValue = "" }
                ),
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                }
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val email = backStackEntry.arguments?.getString("email") ?: ""
                ParticipantReviewScreen(
                    participantName = name,
                    participantEmail = email,
                    onNavigateBack = { tabNavController.popBackStack() }
                )
            }

            composable(
                route = MainRoute.QUIZZES,
                exitTransition = {
                    val target = targetState.destination.route ?: ""
                    if (target.startsWith("create_template") || target.startsWith("game/") || target.startsWith("quiz_review/")) {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    } else {
                        fadeOut(animationSpec = tween(200))
                    }
                },
                popEnterTransition = {
                    val initial = initialState.destination.route ?: ""
                    if (initial.startsWith("create_template") || initial.startsWith("game/") || initial.startsWith("quiz_review/") || initial.startsWith("participant_review/")) {
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
                        if (quiz.mode == QuizMode.ASYNC) {
                            tabNavController.navigate(MainRoute.quizReviewRoute(quiz.id))
                        } else {
                            tabNavController.navigate(MainRoute.gameRoute(quiz.accessCode)) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onReviewQuizClick = { quiz ->
                        tabNavController.navigate(MainRoute.quizReviewRoute(quiz.id))
                    },
                    onCreateTemplateClick = {
                        tabNavController.navigate(MainRoute.createTemplateRoute())
                    },
                    onCreateTemplateAiClick = { prompt ->
                        tabNavController.navigate(MainRoute.createTemplateAiRoute(prompt))
                    },
                    onEditTemplateClick = { template ->
                        tabNavController.navigate(MainRoute.editTemplateRoute(template.id))
                    }
                )
            }

            composable(MainRoute.PROFILE) {
                ProfileScreen()
            }

            composable(
                route = MainRoute.CREATE_TEMPLATE,
                arguments = listOf(
                    navArgument("templateId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("aiPrompt") { type = NavType.StringType; nullable = true; defaultValue = null }
                ),
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
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