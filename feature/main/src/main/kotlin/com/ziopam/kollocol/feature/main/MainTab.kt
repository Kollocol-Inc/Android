package com.ziopam.kollocol.feature.main

data class MainTab(
    val route: String,
    val labelRes: Int,
    val iconRes: Int
)

val mainTabs = listOf(
    MainTab(
        route = MainRoute.HOME,
        labelRes = R.string.tab_home,
        iconRes = R.drawable.home
    ),
    MainTab(
        route = MainRoute.GROUPS,
        labelRes = R.string.tab_groups,
        iconRes = R.drawable.groups
    ),
    MainTab(
        route = MainRoute.QUIZZES,
        labelRes = R.string.tab_quizzes,
        iconRes = R.drawable.compass
    ),
    MainTab(
        route = MainRoute.PROFILE,
        labelRes = R.string.tab_profile,
        iconRes = R.drawable.user
    )
)
