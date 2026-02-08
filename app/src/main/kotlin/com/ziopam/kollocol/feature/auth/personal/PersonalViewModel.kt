package com.ziopam.kollocol.feature.auth.personal

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

sealed interface PersonalUiEvent {
    data object NavigateToMain : PersonalUiEvent
}

@HiltViewModel
class PersonalViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PersonalUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableStateFlow<PersonalUiEvent?>(null)
    val events = _events.asStateFlow()

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
            register()
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

    private fun register(){
        viewModelScope.launch {
            _uiState.update {
                it.copy(error = null)
            }

            val result = userRepository.registerUser(
                _uiState.value.firstName,
                _uiState.value.lastName
            )

            if (result is AppResult.Err) {
                val error = PersonalError.Fields(result.error.toUiText())
                _uiState.update { it.copy(error = error) }
                delay(2000L)
                if (uiState.value.error == error){
                    _uiState.update { it.copy(error = null) }
                }
            } else if (result is AppResult.Ok){
                _events.update { PersonalUiEvent.NavigateToMain }
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
