package com.ziopam.kollocol.feature.auth.code

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.ui.theme.MAX_EDITTEXT_WIDTH

@Composable
fun Otp4CodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    cellSize: DpSize = DpSize(52.dp, 56.dp),
    cellSpacing: Dp = 12.dp,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    animateError: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(code) {
        if (code.length == 4) {
            keyboardController?.hide()
            onComplete()
        }
    }

    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(animateError) {
        shakeX.snapTo(0f)
        if (animateError) {
            shakeX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    val a = 14f
                    0f at 0
                    -a at 50
                    a at 100
                    -a * 0.7f at 150
                    a * 0.7f at 200
                    -a * 0.4f at 250
                    a * 0.4f at 300
                    0f at 400
                }
            )
        }
    }


    fun focusAndShowKeyboard() {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = modifier
            .clickable (
                interactionSource = interactionSource,
                indication = null
            )
            { focusAndShowKeyboard() },
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .focusRequester(focusRequester)
                .widthIn(max = MAX_EDITTEXT_WIDTH.dp)
                .fillMaxWidth()
                .alpha(0f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.graphicsLayer(
                translationX = shakeX.value
            )
        ) {
            repeat(4) { index ->
                val char = code.getOrNull(index)?.toString().orEmpty()

                val isActive = (index == code.length || (code.length == 4 && index == 3))

                OtpCell(
                    char = char,
                    isActive = isActive,
                    size = cellSize,
                    shape = shape,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusAndShowKeyboard() }
                )
            }
        }
    }
}

@Composable
private fun OtpCell(
    char: String,
    isActive: Boolean,
    size: DpSize,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(size.width, size.height)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = shape
            ),
        shape = shape,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = char,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}