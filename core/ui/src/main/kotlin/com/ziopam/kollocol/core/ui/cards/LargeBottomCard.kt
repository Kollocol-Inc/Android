package com.ziopam.kollocol.core.ui.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val cornerRadius = 30.dp

@Composable
fun LargeBottomCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val contentPadding = WindowInsets.systemBars.asPaddingValues()

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
                    start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateRightPadding(LayoutDirection.Ltr),
                    bottom = contentPadding.calculateBottomPadding()
                )
                .padding(horizontal = 20.dp, vertical = 24.dp),
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
