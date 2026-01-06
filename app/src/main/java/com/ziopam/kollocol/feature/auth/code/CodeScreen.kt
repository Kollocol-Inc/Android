package com.ziopam.kollocol.feature.auth.code

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.ui.UiText
import com.ziopam.kollocol.core.ui.asString
import com.ziopam.kollocol.feature.auth.AuthScaffold
import com.ziopam.kollocol.feature.auth.AuthViewModel
import com.ziopam.kollocol.ui.theme.AppTheme
import com.ziopam.kollocol.ui.theme.Typography
import kotlinx.coroutines.delay

private const val timerDuration = 30

@Composable
fun CodeScreen(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    onCodeCompletion: () -> Unit,
    onButtonClick: () -> Unit,
    isLoading: Boolean = false,
    currError: UiText? = null
){
    var restartKey by remember { mutableIntStateOf(0) }
    val timerSeconds = remember { mutableIntStateOf(timerDuration) }

    LaunchedEffect(restartKey) {
        timerSeconds.intValue = timerDuration
        while (timerSeconds.intValue > 0) {
            delay(1_000)
            timerSeconds.intValue--
        }
    }

    AuthScaffold(
        buttonText = if (timerSeconds.intValue > 0)
            stringResource(R.string.send_code_again_timer, timerSeconds.intValue)
        else stringResource(R.string.send_code_again),
        onButtonClick = {
            restartKey++
            onButtonClick()
        },
        isButtonEnabled = timerSeconds.intValue == 0
    ) {
        Text(
            text = stringResource(R.string.verification_code),
            style = Typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isLoading) {
            Otp4CodeInput(
                code = code,
                onCodeChange = onCodeChange,
                onComplete = onCodeCompletion,
                animateError = currError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }


        if (currError == null) {
            val text = stringResource(R.string.code_sent_to_email, email)
            Text(
                text = buildAnnotatedString {
                    append(text)
                    val start = text.indexOf(email)
                    if (start >= 0) {
                        addStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold),
                            start = start,
                            end = start + email.length
                        )
                    }
                },
                style = Typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        } else {
            Text(
                text = currError.asString(),
                style = Typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun CodeScreen(vm: AuthViewModel) {
    val email by vm.email.collectAsState()
    val code by vm.code.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val currError by vm.currError.collectAsState()

    CodeScreen(
        email = email,
        code = code,
        onCodeChange = vm::onCodeChanged,
        onCodeCompletion = vm::verifyCode,
        onButtonClick = {},
        isLoading = isLoading,
        currError = currError
    )
}

@Preview
@Composable
private fun CodeScreenPreview(){
    val email = "example@mail.com"
    var code by remember { mutableStateOf("") }

    AppTheme { CodeScreen(email, code, { code = it }, {}, {}) }
}