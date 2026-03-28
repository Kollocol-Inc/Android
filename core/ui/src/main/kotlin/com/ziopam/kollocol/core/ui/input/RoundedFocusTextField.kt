package com.ziopam.kollocol.core.ui.input

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.theme.MAX_EDITTEXT_WIDTH

@Composable
fun RoundedFocusTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(25.dp)
    val bg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val border = if (isFocused) MaterialTheme.colorScheme.primary else bg

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = (if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .widthIn(max = MAX_EDITTEXT_WIDTH.dp)
            .then(modifier)
            .border(width = 2.dp, color = border, shape = shape),
        singleLine = true,
        interactionSource = interactionSource,
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = textStyle
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { if (imeAction == ImeAction.Next) onImeAction() },
            onDone = { if (imeAction == ImeAction.Done) onImeAction() }
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
