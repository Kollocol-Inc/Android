package com.ziopam.kollocol.feature.main.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.input.RoundedFocusTextField
import com.ziopam.kollocol.feature.main.R

@Composable
internal fun ProfileEditNameDialog(
    initialFirstName: String,
    initialLastName: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf(initialFirstName) }
    var lastName by rememberSaveable { mutableStateOf(initialLastName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_name_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RoundedFocusTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = stringResource(R.string.first_name),
                    imeAction = ImeAction.Next,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
                RoundedFocusTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = stringResource(R.string.last_name),
                    imeAction = ImeAction.Done,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank()) {
                        onConfirm(firstName.trim(), lastName.trim())
                    }
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
