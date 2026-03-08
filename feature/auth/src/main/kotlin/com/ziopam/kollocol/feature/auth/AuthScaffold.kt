package com.ziopam.kollocol.feature.auth

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.AppScaffold
import com.ziopam.kollocol.core.ui.buttons.DefaultButton

@Composable
fun AuthScaffold(
    buttonText: String,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean,
    content: @Composable () -> Unit
){
    val focusManager = LocalFocusManager.current

    AppScaffold {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() })},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text= stringResource(R.string.app_name),
                style= MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            )

            Spacer(Modifier.weight(1f))
            content()
            Spacer(Modifier.weight(1f))

            DefaultButton(
                text = buttonText,
                onClick = onButtonClick,
                isButtonEnabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}