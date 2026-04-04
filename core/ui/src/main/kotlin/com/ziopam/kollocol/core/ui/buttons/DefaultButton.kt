package com.ziopam.kollocol.core.ui.buttons

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.core.ui.theme.MAX_BUTTON_WIDTH

@Composable
fun DefaultButton(
    text: String,
    onClick: () -> Unit,
    isButtonEnabled: Boolean,
    isWidthLimited: Boolean = true,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isButtonEnabled,
        modifier =
            if (isWidthLimited) {
                Modifier.widthIn(max = MAX_BUTTON_WIDTH.dp).then(modifier)
            } else { modifier },
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(text)
    }
}

@Composable
@Preview
private fun DefaultButtonPreview() {
    AppTheme {
        DefaultButton(
            text = "Кнопка",
            onClick = {},
            isButtonEnabled = true
        )
    }
}