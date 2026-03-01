package com.ziopam.kollocol.feature.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ziopam.kollocol.core.ui.clickableNoIndication

@Composable
fun MainBottomBar(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .dropShadow(
                shape = RoundedCornerShape(24.dp),
                shadow = Shadow(
                    radius = 10.dp,
                    spread = if (isSystemInDarkTheme()) 1.dp else 0.dp,
                    color = DefaultShadowColor.copy(alpha = 0.2f),
                    offset = DpOffset(
                        x = 0.dp,
                        y = if (isSystemInDarkTheme()) 2.dp else 6.dp),
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val tabsCount = mainTabs.size

                val selectedIndex = mainTabs.indexOfFirst { it.route == currentRoute }
                    .let { if (it < 0) 0 else it }

                val tabWidth = maxWidth / tabsCount

                val indicatorOffsetX by animateDpAsState(
                    targetValue = selectedIndex * tabWidth,
                    animationSpec = tween(durationMillis = 220),
                    label = "bottomBarIndicatorOffset"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    mainTabs.forEach { tab ->
                        val selected = tab.route == currentRoute

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickableNoIndication {
                                    navController.navigateToMainTab(tab.route)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(26.dp),
                                painter = painterResource(tab.iconRes),
                                contentDescription = stringResource(tab.labelRes),
                                tint = if (selected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(vertical = 2.dp)
                        .zIndex(0f)
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = indicatorOffsetX)
                            .padding(vertical = 4.dp)
                            .width(tabWidth)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(22.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                }
            }
        }
    }
}

fun NavHostController.navigateToMainTab(route: String) {
    navigate(route) {
        val start = graph.startDestinationId
        popUpTo(start) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
