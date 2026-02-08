package com.ziopam.kollocol.feature.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.ui.AppScaffold
import com.ziopam.kollocol.core.ui.theme.MAX_BUTTON_WIDTH
import com.ziopam.kollocol.core.ui.theme.Typography

@Composable
fun AuthScaffold(
    buttonText: String,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean,
    content: @Composable () -> Unit
){
    AppScaffold {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text= stringResource(R.string.app_name),
                style= Typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            )

            Spacer(Modifier.weight(1f))
            content()
            Spacer(Modifier.weight(1f))

            Button(
                onClick = onButtonClick,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .widthIn(max = MAX_BUTTON_WIDTH.dp)
                    .fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}