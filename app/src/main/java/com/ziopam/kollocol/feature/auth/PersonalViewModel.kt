package com.ziopam.kollocol.feature.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.ui.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val MIN_PERSONAL_NAME_LENGTH = 2
private const val MAX_PERSONAL_NAME_LENGTH = 50

data class PersonalUiState(
    val firstName: String = "",
    val lastName: String = "",
    val avatarUri: Uri? = null,
    val error: PersonalError? = null,
)

sealed interface PersonalError {
    data class Avatar(val message: UiText) : PersonalError
    data class Fields(val message: UiText) : PersonalError
}

@HiltViewModel
class PersonalViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PersonalUiState())
    val uiState = _uiState.asStateFlow()

    fun onFirstNameChanged(input: String) = _uiState.update {
        it.copy(firstName = sanitizeName(input), error = if (it.error is PersonalError.Fields) null else it.error)
    }

    fun onLastNameChanged(input: String) = _uiState.update {
        it.copy(lastName = sanitizeName(input), error = if (it.error is PersonalError.Fields) null else it.error)
    }

    fun onAvatarSelected(uri: Uri?) = _uiState.update {
        it.copy(avatarUri = uri, error = if (it.error is PersonalError.Avatar) null else it.error)
    }

    fun onAvatarError(error: UiText) = _uiState.update {
        it.copy(error = PersonalError.Avatar(error))
    }

    fun onAvatarRemove(){
        _uiState.update {
            it.copy(avatarUri = null, error = if (it.error is PersonalError.Avatar) null else it.error)
        }
    }

    fun onButtonClick(){
        _uiState.update {
            it.copy(
                firstName = it.firstName.trim().replace(Regex("\\s+"), " "),
                lastName = it.lastName.trim().replace(Regex("\\s+"), " ")
            )
        }

        if (_uiState.value.firstName.length in MIN_PERSONAL_NAME_LENGTH..MAX_PERSONAL_NAME_LENGTH &&
            _uiState.value.lastName.length in MIN_PERSONAL_NAME_LENGTH..MAX_PERSONAL_NAME_LENGTH) {
            _uiState.update {
                it.copy(error = null)
            }
        } else {
            _uiState.update {
                it.copy(error = PersonalError.Fields(
                    UiText.StringRes(
                        R.string.name_length_error,
                        listOf(MIN_PERSONAL_NAME_LENGTH, MAX_PERSONAL_NAME_LENGTH)
                    )))
            }
        }
    }

    fun isButtonEnabled(state: PersonalUiState): Boolean {
        return state.firstName.isNotBlank() &&
                state.lastName.isNotBlank() &&
                state.error !is PersonalError.Fields
    }

    private fun sanitizeName(input: String): String {
        return input
            .replace("\n", " ")
            .replace("\r", " ")
            .take(MAX_PERSONAL_NAME_LENGTH)
    }
}
