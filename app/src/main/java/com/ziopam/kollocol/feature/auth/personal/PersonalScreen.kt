package com.ziopam.kollocol.feature.auth.personal

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.asString
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.core.ui.theme.MAX_EDITTEXT_WIDTH
import com.ziopam.kollocol.core.ui.theme.Typography

@Composable
fun PersonalScreen(
    state: PersonalUiState,
    onButtonClick: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAvatarSelected: (Uri?) -> Unit,
    onAvatarError: (UiText) -> Unit,
    onAvatarRemove: () -> Unit,
    isButtonEnabled: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    _root_ide_package_.com.ziopam.kollocol.feature.auth.AuthScaffold(
        buttonText = stringResource(R.string.perform_registration),
        onButtonClick = onButtonClick,
        isButtonEnabled = isButtonEnabled
    ) {
        Text(
            text = stringResource(R.string.registration),
            style = Typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        SignUpAvatar(
            state = state,
            onAvatarSelected = onAvatarSelected,
            onAvatarError = onAvatarError,
            onAvatarRemove = onAvatarRemove
        )

        val lastNameRequester = remember { FocusRequester() }
        val textFieldModifier = Modifier
            .widthIn(MAX_EDITTEXT_WIDTH.dp)
            .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)

        RoundedFocusTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            placeholder = stringResource(R.string.name),
            imeAction = ImeAction.Next,
            onImeAction = { lastNameRequester.requestFocus() },
            modifier = textFieldModifier
        )

        RoundedFocusTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            placeholder = stringResource(R.string.lastName),
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
                text = state.error.message.asString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun PersonalScreen(
    vm: PersonalViewModel,
) {
    val state = vm.uiState.collectAsStateWithLifecycle()

    PersonalScreen(
        state = state.value,
        onFirstNameChange = vm::onFirstNameChanged,
        onLastNameChange = vm::onLastNameChanged,
        onAvatarSelected = vm::onAvatarSelected,
        onAvatarError = vm::onAvatarError,
        onAvatarRemove = vm::onAvatarRemove,
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
        avatarUri = Uri.EMPTY,
        error = null
    ))}

    AppTheme {
        PersonalScreen(
            state = state.value,
            onFirstNameChange = { state.value = state.value.copy(firstName = it) },
            onLastNameChange = { state.value = state.value.copy(lastName = it) },
            onAvatarSelected = {},
            onAvatarRemove = {},
            onAvatarError = {},
            onButtonClick = {}
        )
    }
}
