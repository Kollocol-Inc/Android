package com.ziopam.kollocol.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val MAX_EMAIL_LENGTH = 254

// TODO Link ViewModel with Graph
@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel(){
    var email by mutableStateOf("")
        private set

    fun onEmailChanged(input: String) {
        email = input
            .lowercase()
            .filterNot { it.isWhitespace() }
            .filter { it.code < 128 }
            .take(MAX_EMAIL_LENGTH)
    }

    fun isEmailValid(input: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }
}