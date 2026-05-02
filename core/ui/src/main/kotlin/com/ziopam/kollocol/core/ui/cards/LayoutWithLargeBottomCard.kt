package com.ziopam.kollocol.core.ui.cards

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.LayoutDirection
import com.ziopam.kollocol.core.ui.contentPadding

@Composable
fun LayoutWithLargeBottomCard(
    contentAbove: @Composable BoxScope.() -> Unit = {},
    scrollable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val padding  = WindowInsets.systemBars.asPaddingValues()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() })
    }) {
        Box(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = padding.calculateRightPadding(LayoutDirection.Ltr)
                )
                .padding(contentPadding),
            content = contentAbove
        )

        LargeBottomCard(
            modifier = Modifier.weight(1f),
            scrollable = scrollable,
            content = content
        )
    }
}