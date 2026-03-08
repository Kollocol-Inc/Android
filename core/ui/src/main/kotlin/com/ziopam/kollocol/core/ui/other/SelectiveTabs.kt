package com.ziopam.kollocol.core.ui.other

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex

@Composable
fun SelectiveTabs(
    tabsCount: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    selectionWidthPadding: Dp = 10.dp,
    insideVerticalPadding: Dp = 20.dp,
    backGroundColor: Color = MaterialTheme.colorScheme.surface,
    selectionColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
    tabsContent: @Composable RowScope.() -> Unit
) {
    val tabsCount = tabsCount.coerceAtLeast(1)
    val selectedIndex = selectedIndex.coerceIn(0, tabsCount - 1)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = backGroundColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val tabWidth = maxWidth / tabsCount

                val indicatorOffsetX by animateDpAsState(
                    targetValue = selectedIndex * tabWidth + selectionWidthPadding / 2,
                    animationSpec = tween(durationMillis = 220),
                    label = "bottomBarIndicatorOffset"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = insideVerticalPadding)
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabsContent()
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .zIndex(0f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .offset(x = indicatorOffsetX)
                            .padding(vertical = 4.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(22.dp))
                            .background(selectionColor)
                    )
                }
            }
        }
    }
}
