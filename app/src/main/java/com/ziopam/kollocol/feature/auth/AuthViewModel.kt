package com.ziopam.kollocol.feature.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val MAX_EMAIL_LENGTH = 254

// TODO Вынести текстовые данные в ресурсы
@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel(){
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _currError = MutableStateFlow<String?>(null)
    val currError = _currError.asStateFlow()

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
            _currError.update { "Неверный формат email" }
            return false
        }
    }
}