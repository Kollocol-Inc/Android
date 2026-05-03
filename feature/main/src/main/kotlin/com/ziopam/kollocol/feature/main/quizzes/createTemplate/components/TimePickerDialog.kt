package com.ziopam.kollocol.feature.main.quizzes.createTemplate.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimePickerDialog(
    currentSec: Int,
    okText: String,
    cancelText: String,
    timeLabel: String,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var minutesText by remember { mutableStateOf("%02d".format(currentSec / 60)) }
    var secondsText by remember { mutableStateOf("%02d".format(currentSec % 60)) }

    val minutes = minutesText.toIntOrNull()?.coerceIn(0, 9) ?: 0
    val seconds = secondsText.toIntOrNull()?.coerceIn(0, 59) ?: 0

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeUnitPicker(
                        text = minutesText,
                        onTextChange = { minutesText = it },
                        onIncrement = { minutesText = "%02d".format((minutes + 1).coerceAtMost(9)) },
                        onDecrement = { minutesText = "%02d".format((minutes - 1).coerceAtLeast(0)) }
                    )

                    Text(text = ":", style = MaterialTheme.typography.displaySmall)

                    TimeUnitPicker(
                        text = secondsText,
                        onTextChange = { secondsText = it },
                        onIncrement = { secondsText = "%02d".format(((seconds + 5) / 5 * 5).coerceAtMost(55)) },
                        onDecrement = { secondsText = "%02d".format(((seconds - 1) / 5 * 5).coerceAtLeast(0)) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text(cancelText) }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = {
                        val total = (minutes * 60 + seconds).coerceAtLeast(5)
                        onConfirm(total)
                    }) { Text(okText) }
                }
            }
        }
    }
}

@Composable
private fun TimeUnitPicker(
    text: String,
    onTextChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val outline = MaterialTheme.colorScheme.outline
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = onIncrement) {
            Text(text = "+", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
        OutlinedTextField(
            value = text,
            onValueChange = { v -> onTextChange(v.filter { it.isDigit() }.take(2)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(72.dp),
            textStyle = MaterialTheme.typography.headlineLarge.copy(
                textAlign = TextAlign.Center,
                color = onSurface,
                fontWeight = FontWeight.Normal
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = outline
            )
        )
        IconButton(onClick = onDecrement) {
            Text(text = "−", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
