package com.ziopam.kollocol.feature.auth

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.avatar.rememberAvatarPicker
import com.ziopam.kollocol.ui.theme.AppTheme
import com.ziopam.kollocol.ui.theme.MAX_EDITTEXT_WIDTH
import com.ziopam.kollocol.ui.theme.Typography

// TODO Вынести текстовые данные в ресурсы
// TODO Добавить возможность удалить аватарку
@Composable
fun PersonalScreen(
    state: PersonalUiState,
    onButtonClick: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAvatarSelected: (Uri?) -> Unit,
    onAvatarError: (String) -> Unit,
    isButtonEnabled: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    AuthScaffold(
        buttonText = "Зарегистрироваться",
        onButtonClick = onButtonClick,
        isButtonEnabled = isButtonEnabled
    ) {
        Text(
            text="Регистрация",
            style= Typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        val pickAvatar = rememberAvatarPicker(
            onAvatarSelected = { uri -> onAvatarSelected(uri) },
            onError = { msg -> onAvatarError(msg) }
        )
        AvatarPicker(
            avatarUri = state.avatarUri,
            onClick = pickAvatar,
            borderColor = if (state.error is PersonalError.Avatar)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        if (state.error is PersonalError.Avatar) {
            Text(
                text = state.error.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            )
        }

        val lastNameRequester = remember { FocusRequester() }
        val textFieldModifier = Modifier
            .widthIn(MAX_EDITTEXT_WIDTH.dp)
            .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)

        RoundedFocusTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            placeholder = "Имя",
            imeAction = ImeAction.Next,
            onImeAction = { lastNameRequester.requestFocus() },
            modifier = textFieldModifier
        )

        RoundedFocusTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            placeholder = "Фамилия",
            focusRequester = lastNameRequester,
            imeAction = ImeAction.Done,
            onImeAction = {
                focusManager.clearFocus()
                onButtonClick()
            },
            modifier = textFieldModifier
        )


        if (state.error is PersonalError.Fields) {
            Text(
                text = state.error.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun PersonalScreen(
    vm: PersonalViewModel,
    onButtonClick: () -> Unit,
) {
    val state = vm.uiState.collectAsStateWithLifecycle()

    PersonalScreen(
        state = state.value,
        onFirstNameChange = vm::onFirstNameChanged,
        onLastNameChange = vm::onLastNameChanged,
        onAvatarSelected = vm::onAvatarSelected,
        onAvatarError = vm::onAvatarError,
        onButtonClick = vm::onButtonClick,
        isButtonEnabled = vm.isButtonEnabled(state.value)
    )
}

@Preview
@Composable
private fun PersonalScreenPreview() {
    val state = remember { mutableStateOf(PersonalUiState(
        firstName = "",
        lastName = "",
        avatarUri = null,
        error = null
    ))}

    AppTheme {
        PersonalScreen(
            state = state.value,
            onFirstNameChange = { state.value = state.value.copy(firstName = it) },
            onLastNameChange = { state.value = state.value.copy(lastName = it) },
            onAvatarSelected = {},
            onAvatarError = {},
            onButtonClick = {}
        )
    }
}
