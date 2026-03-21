package com.ziopam.kollocol.feature.main

data class MainTab(
    val route: String,
    val labelRes: Int,
    val iconRes: Int,
    val iconResFilled: Int = iconRes
)

val mainTabs = listOf(
    MainTab(
        route = MainRoute.HOME,
        labelRes = R.string.tab_home,
        iconRes = R.drawable.home,
        iconResFilled = R.drawable.home_filled
    ),
    MainTab(
        route = MainRoute.GROUPS,
        labelRes = R.string.tab_groups,
        iconRes = R.drawable.groups,
        iconResFilled = R.drawable.groups_filled
    ),
    MainTab(
        route = MainRoute.QUIZZES,
        labelRes = R.string.tab_quizzes,
        iconRes = R.drawable.compass,
        iconResFilled = R.drawable.compass_filled
    ),
    MainTab(
        route = MainRoute.PROFILE,
        labelRes = R.string.tab_profile,
        iconRes = R.drawable.user,
        iconResFilled = R.drawable.user_filled
    )
)
