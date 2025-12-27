package com.ziopam.kollocol.feature.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.ui.theme.AppTheme
import com.ziopam.kollocol.ui.theme.MAX_EDITTEXT_WIDTH
import com.ziopam.kollocol.ui.theme.Typography

// TODO Вынести текстовые данные в ресурсы
@Composable
fun EmailScreen(
    value: String,
    onValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    isButtonEnabled: Boolean = value.isNotEmpty(),
    currError: String? = null
) {
    AuthScaffold(
        "Получить код",
        onButtonClick,
        isButtonEnabled
    ) {
        Text(
            text="Email",
            style=Typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.widthIn(max = MAX_EDITTEXT_WIDTH.dp).fillMaxWidth(),

            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                color = if (currError == null) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.error
            ),

            singleLine = true,

            placeholder = {
                Text(
                    text = "example@mail.com",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),

            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,

                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        if (currError != null) {
            Text(
                text = currError,
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EmailScreen(vm: AuthViewModel, onNavigate: () -> Unit){
    val email by vm.email.collectAsState()
    val currError by vm.currError.collectAsState()

    EmailScreen(
        value = email,
        onValueChange = vm::onEmailChanged,
        isButtonEnabled = email.isNotEmpty() && (currError == null),
        onButtonClick = {
            if (vm.isEmailValid(email)){
                onNavigate()
            }
        },
        currError = currError
    )
}

@PreviewLightDark
@Composable
fun EmailScreenPreview(){
    var email by remember { mutableStateOf("") }

    AppTheme {
        EmailScreen(email, { email = it }, {}, isButtonEnabled = true)
    }
}