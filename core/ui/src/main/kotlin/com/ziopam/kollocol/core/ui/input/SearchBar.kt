package com.ziopam.kollocol.core.ui.input

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.R
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.theme.AppTheme

private val iconSize = 20.dp
private val shape = RoundedCornerShape(24.dp)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchBar(
    placeholder: String,
    text: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val focusManager = LocalFocusManager.current

    val bg = MaterialTheme.colorScheme.primaryContainer
    val borderColor = if (isFocused) MaterialTheme.colorScheme.primary else bg

    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && isFocused) {
            focusManager.clearFocus()
        }
    }

    TextField(
        value = text,
        leadingIcon = {
            Icon(
                painterResource(R.drawable.search),
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        },
        placeholder = {
            Text(
                placeholder,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)
            )
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                Icon(
                    painterResource(R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .clickableNoIndication{ onQueryChange("") }
                )
            }
        },
        onValueChange = onQueryChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onSearch = { focusManager.clearFocus() }
        ),
        shape = shape,
        modifier = modifier
            .height(50.dp)
            .border(1.dp, borderColor, shape),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = bg,
            unfocusedContainerColor = bg,
            disabledContainerColor = bg,

            focusedIndicatorColor = bg,
            unfocusedIndicatorColor = bg,
            disabledIndicatorColor = bg,

            cursorColor = MaterialTheme.colorScheme.primary
        ),
        interactionSource = interactionSource
    )
}

@Preview
@Composable
private fun SearchBarPreview(){
    var text by mutableStateOf("")

    AppTheme {
        SearchBar(
            placeholder = "Поиск",
            text = text,
            onQueryChange = { text = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}