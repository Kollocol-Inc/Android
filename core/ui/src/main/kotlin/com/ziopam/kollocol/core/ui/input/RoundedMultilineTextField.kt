package com.ziopam.kollocol.core.ui.input

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun RoundedMultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minLines: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(16.dp)
    val bg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val border = if (isFocused) MaterialTheme.colorScheme.primary else bg

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.border(width = 2.dp, color = border, shape = shape),
        enabled = enabled,
        minLines = minLines,
        interactionSource = interactionSource,
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = textStyle
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            imeAction = ImeAction.Default
        ),
        shape = shape,
        textStyle = textStyle,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = bg,
            unfocusedContainerColor = bg,
            disabledContainerColor = bg,
            focusedIndicatorColor = bg,
            unfocusedIndicatorColor = bg,
            disabledIndicatorColor = bg,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
    )
}
