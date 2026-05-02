package com.ziopam.kollocol.feature.main.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.input.RoundedMultilineTextField
import com.ziopam.kollocol.feature.main.R

@Composable
fun AiPromptDialog(
    title: String,
    hint: String,
    prompt: String,
    onPromptChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            RoundedMultilineTextField(
                value = prompt,
                onValueChange = onPromptChange,
                placeholder = hint,
                modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (prompt.isNotBlank()) onConfirm()
                }
            ) {
                Text(stringResource(R.string.generate))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
