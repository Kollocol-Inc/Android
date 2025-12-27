package com.ziopam.kollocol.feature.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val MAX_EMAIL_LENGTH = 254


@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel(){
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun onEmailChanged(input: String) {
        _email.value = input
            .lowercase()
            .filterNot { it.isWhitespace() }
            .filter { it.code < 128 }
            .take(MAX_EMAIL_LENGTH)
    }

    fun isEmailValid(input: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }
}