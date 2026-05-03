package com.ziopam.kollocol.core.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ziopam.kollocol.core.ui.theme.AppTheme

@Composable
fun DefaultDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String? = null,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { onCancel?.invoke() ?: onConfirm() },
        title = { Text(title) },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Justify,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineBreak = LineBreak.Paragraph,
                    hyphens = Hyphens.Auto
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = if (onCancel != null && cancelText != null) {
            {
                TextButton(onClick = onCancel) {
                    Text(cancelText)
                }
            }
        } else null
    )
}

@Preview
@Composable
fun DangerousConfirmationDialogPreview(){
    AppTheme {
        DefaultDialog(
            title = "Какое-то уведомление",
            message = "Вы уверены, что хотите выйти?",
            confirmText = "Выйти",
            onConfirm = {},
            onCancel = {}
        )
    }
}