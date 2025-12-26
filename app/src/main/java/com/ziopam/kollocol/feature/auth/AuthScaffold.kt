package com.ziopam.kollocol.feature.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.ui.AppScaffold
import com.ziopam.kollocol.ui.theme.Typography

@Composable
fun AuthScaffold(
    onButtonClick: () -> Unit,
    content: @Composable () -> Unit
){
    AppScaffold {
        Column (Modifier.fillMaxSize()) {
            Text(
                text="Kollocol",
                style= Typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp)
            )

            Spacer(Modifier.weight(1f))
            content()
            Spacer(Modifier.weight(1f))

            Button(
                onClick=onButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Далее")
            }
        }
    }
}