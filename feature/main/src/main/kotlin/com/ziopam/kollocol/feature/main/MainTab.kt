package com.ziopam.kollocol.feature.main

import com.ziopam.kollocol.core.ui.R as CoreR

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
        iconRes = CoreR.drawable.home,
        iconResFilled = CoreR.drawable.home_filled
    ),
    MainTab(
        route = MainRoute.GROUPS,
        labelRes = R.string.tab_groups,
        iconRes = CoreR.drawable.groups,
        iconResFilled = CoreR.drawable.groups_filled
    ),
    MainTab(
        route = MainRoute.QUIZZES,
        labelRes = R.string.tab_quizzes,
        iconRes = CoreR.drawable.compass,
        iconResFilled = CoreR.drawable.compass_filled
    ),
    MainTab(
        route = MainRoute.PROFILE,
        labelRes = R.string.tab_profile,
        iconRes = CoreR.drawable.user,
        iconResFilled = CoreR.drawable.user_filled
    )
)
