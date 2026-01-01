package com.ziopam.kollocol.feature.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val MAX_PERSONAL_NAME_LENGTH = 100

data class PersonalUiState(
    val firstName: String = "",
    val lastName: String = "",
    val avatarUri: Uri? = null,
    val error: PersonalError? = null,
    val isButtonEnabled: Boolean = firstName.isNotBlank() && lastName.isNotBlank()
)

sealed interface PersonalError {
    data class Avatar(val message: String) : PersonalError
    data class Fields(val message: String) : PersonalError
}

@HiltViewModel
class PersonalViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PersonalUiState())
    val uiState = _uiState.asStateFlow()

    fun onFirstNameChanged(input: String) = _uiState.update {
        it.copy(firstName = sanitizeName(input), error = null)
    }

    fun onLastNameChanged(input: String) = _uiState.update {
        it.copy(lastName = sanitizeName(input), error = null)
    }

    fun onAvatarSelected(uri: Uri?) = _uiState.update {
        it.copy(avatarUri = uri, error = if (it.error is PersonalError.Avatar) null else it.error)
    }

    fun onAvatarError(error: String) = _uiState.update {
        it.copy(error = PersonalError.Avatar(error))
    }

    private fun sanitizeName(input: String): String {
        return input
            .replace("\n", " ")
            .replace("\r", " ")
            .take(MAX_PERSONAL_NAME_LENGTH)
    }
}
