package com.ziopam.kollocol.feature.auth

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CodeScreen(){
    AuthScaffold({}, false) {
        Text("Hello world!")
    }
}

@Composable
fun CodeScreen(vm: AuthViewModel){
    Log.d("CodeScreen", "Email from ViewModel: ${vm.email}")
    AuthScaffold({}, false) {
        Text("Введенная почта: ${vm.email}")
    }
}