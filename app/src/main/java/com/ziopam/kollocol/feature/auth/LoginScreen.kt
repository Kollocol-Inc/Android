package com.ziopam.kollocol.feature.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.ui.AppScaffold

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    AppScaffold {
        Column(Modifier.padding(16.dp)) {
            Text("AUTH / Login")
            Spacer(Modifier.height(12.dp))
            Button(onClick = onLoginSuccess) {
                Text("Успешно войти (заглушка)")
            }
        }
    }
}