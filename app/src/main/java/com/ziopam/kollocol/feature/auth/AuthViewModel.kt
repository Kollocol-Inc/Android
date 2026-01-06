package com.ziopam.kollocol.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.ui.UiText
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
}

private const val MAX_EMAIL_LENGTH = 254

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel(){
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
            .lowercase()
            .filterNot { it.isWhitespace() }
            .filter { it.code < 128 }
            .take(MAX_EMAIL_LENGTH)
        _currError.update { null }
    }

    fun isEmailValid(input: String): Boolean {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()){
            _currError.update { null }
            return true
        } else {
            _currError.update { UiText.StringRes(R.string.wrong_email_format) }
            return false
        }
    }

    fun onCodeChanged(input: String) {
        _code.value = input.filter { it.isDigit() }.take(4)
    }

    fun verifyCode(){
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000L)
            if (_code.value == "2222") {
                _events.emit(AuthUiEvent.NavigateToPersonal)
            } else {
                _currError.update { UiText.StringRes(R.string.wrong_verification_code) }
                _code.value = ""
                _isLoading.value = false
            }
        }
    }

    fun clearError(){
        _currError.value = null
    }
}