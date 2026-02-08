package com.ziopam.kollocol.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.toUiText
import com.ziopam.kollocol.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiEvent {
    data object NavigateToPersonal : AuthUiEvent
    data object NavigateToMain: AuthUiEvent
    data object NavigateToCode : AuthUiEvent
}

private const val MAX_EMAIL_LENGTH = 254

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){
    private val _email = MutableStateFlow("")
    private val _code = MutableStateFlow("")
    private val _currError = MutableStateFlow<UiText?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _events = MutableSharedFlow<AuthUiEvent>(replay = 0)
    val email = _email.asStateFlow()
    val code = _code.asStateFlow()
    val currError = _currError.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val events = _events.asSharedFlow()

    fun onEmailChanged(input: String) {
        _email.value = input
            .filterNot { it.isWhitespace() }
            .filter { it.code < 128 }
            .take(MAX_EMAIL_LENGTH)
        _currError.update { null }
    }

    fun requestCode(){
        viewModelScope.launch {
            if (!isEmailValid(_email.value)) return@launch
            _isLoading.value = true
            val sendToEmail = _email.value
            val result = authRepository.login(sendToEmail)

            if (result is AppResult.Err) {
                when (result.error) {
                    is AppError.BadRequest -> {
                        _currError.update { UiText.StringRes(R.string.wrong_email_format) }
                    }
                    else -> {
                        _currError.update { result.error.toUiText() }
                        delay(2000L)
                        if (_currError.value == result.error.toUiText()){
                            _currError.update { null }
                        }
                    }
                }
            } else {
                _events.emit(AuthUiEvent.NavigateToCode)
                _email.value = sendToEmail
            }
            _isLoading.value = false
        }
    }

    fun isRequestCodeEnabled(): Boolean {
        return _email.value.isNotEmpty() && _currError.value == null && !_isLoading.value
    }

    fun onCodeChanged(input: String) {
        _code.value = input.filter { it.isDigit() }.take(4)
    }

    fun verifyCode(){
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.verifyCode(_email.value, _code.value)
            _code.update { "" }
            if (result is AppResult.Err) {
                when (result.error) {
                    is AppError.BadRequest -> {
                        _currError.update { UiText.StringRes(R.string.wrong_verification_code) }
                    }
                    else -> {
                        _currError.update { result.error.toUiText() }
                        delay(2000L)
                        if (_currError.value == result.error.toUiText()){
                            _currError.update { null }
                        }
                    }
                }
                _isLoading.value = false
            } else if (result is AppResult.Ok) {
                if (result.value.isRegistered) {
                    _events.emit(AuthUiEvent.NavigateToMain)
                } else {
                    _events.emit(AuthUiEvent.NavigateToPersonal)
                }
            }
        }
    }

    fun clearError(){
        _currError.value = null
    }

    private fun isEmailValid(input: String): Boolean {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()){
            _currError.update { null }
            return true
        } else {
            _currError.update { UiText.StringRes(R.string.wrong_email_format) }
            return false
        }
    }
}