package com.ziopam.kollocol.core.ui.input

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ziopam.kollocol.core.ui.theme.AppTheme

@Composable
fun Switch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors().copy(
            uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier
    )
}

@Preview
@Composable
private fun SwitchPreview() {
    AppTheme {
        Switch(
            isChecked = false,
            onCheckedChange = {}
        )
    }
}