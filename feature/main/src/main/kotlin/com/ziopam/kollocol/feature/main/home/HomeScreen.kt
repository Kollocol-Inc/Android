package com.ziopam.kollocol.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.input.Otp4CodeInput
import com.ziopam.kollocol.feature.main.MainScaffoldPreview

@Composable
fun HomeScreen() {
    LayoutWithLargeBottomCard(
        contentAbove = { Otp4CodeInput("", {}, {}) }
    ) { }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    MainScaffoldPreview { HomeScreen() }
}