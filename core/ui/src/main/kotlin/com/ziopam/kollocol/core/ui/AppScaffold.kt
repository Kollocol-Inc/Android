package com.ziopam.kollocol.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppScaffold(
    bottomBar: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
){
    Scaffold(
        bottomBar = { bottomBar?.invoke() }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(10.dp),
            contentAlignment = Alignment.Center
        ){
            content()
        }
    }
}