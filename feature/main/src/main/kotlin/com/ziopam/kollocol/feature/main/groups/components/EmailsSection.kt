package com.ziopam.kollocol.feature.main.groups.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun EmailsSection(
    emails: List<String>,
    onAddEmail: () -> Unit,
    onEmailChange: (Int, String) -> Unit,
    onRemoveEmail: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.group_participants),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        CircleIconButton(
            onClick = onAddEmail,
            icon = ImageVector.vectorResource(CoreR.drawable.add),
            contentDescription = null,
            size = 40.dp
        )
    }

    emails.forEachIndexed { index, email ->
        Spacer(modifier = Modifier.height(8.dp))
        EmailInputRow(
            email = email,
            onEmailChange = { onEmailChange(index, it) },
            onRemove = { onRemoveEmail(index) }
        )
    }
}

@Composable
private fun EmailInputRow(
    email: String,
    onEmailChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val isInvalid = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompactOutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.add_participant_email_hint),
            modifier = Modifier.weight(1f),
            isError = isInvalid
        )
        Icon(
            imageVector = ImageVector.vectorResource(CoreR.drawable.close),
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .size(20.dp)
                .clickableNoIndication(onClick = onRemove),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CompactOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    isError: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                isError = isError,
                placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = isError,
                        interactionSource = interactionSource,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            )
        }
    )
}
