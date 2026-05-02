package com.ziopam.kollocol.core.ui.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val LocalExtraBottomPadding = compositionLocalOf { 0.dp }

private val cornerRadius = 30.dp

@Composable
fun LargeBottomCard(
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val extraBottomPadding = LocalExtraBottomPadding.current
    val bottomPadding: Dp = maxOf(
        systemBarsPadding.calculateBottomPadding(),
        extraBottomPadding
    )
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = cornerRadius,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = systemBarsPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = systemBarsPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = bottomPadding
                )
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 3.dp)
                .then(if (scrollable) Modifier.verticalScroll(scrollState) else Modifier),
            content = content
        )
    }
}

@Preview
@Composable
private fun LargeBottomCardPreview() {
    LargeBottomCard {
        Text("LargeBottomCard")
    }
}
