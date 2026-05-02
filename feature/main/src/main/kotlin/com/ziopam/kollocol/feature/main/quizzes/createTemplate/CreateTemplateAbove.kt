package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.buttons.DoubleCircleIconButton
import com.ziopam.kollocol.core.ui.contentPadding
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun CreateTemplateAbove(
    isLoading: Boolean,
    isEditMode: Boolean,
    onBackAttempt: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding() / 2
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            onClick = onBackAttempt,
            icon = ImageVector.vectorResource(CoreR.drawable.arrow_back),
            contentDescription = stringResource(CoreR.string.back),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = if (isEditMode) stringResource(R.string.template_editing) else stringResource(R.string.template_creation),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 5.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(end = 12.dp).size(24.dp))
        } else if (isEditMode) {
            DoubleCircleIconButton(
                onLeftClick = onDeleteClick,
                leftIcon = ImageVector.vectorResource(CoreR.drawable.delete),
                leftContentDescription = stringResource(R.string.delete_template),
                leftTint = MaterialTheme.colorScheme.error,
                onRightClick = onSaveClick,
                rightIcon = ImageVector.vectorResource(CoreR.drawable.check),
                rightContentDescription = stringResource(R.string.create_template),
                rightTint = ExtraColors.affirmative
            )
        } else {
            CircleIconButton(
                onClick = onSaveClick,
                icon = ImageVector.vectorResource(CoreR.drawable.check),
                tint = ExtraColors.affirmative,
                contentDescription = stringResource(R.string.create_template)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTemplateAbovePreview() {
    AppTheme {
        CreateTemplateAbove(
            isLoading = false,
            isEditMode = false,
            onBackAttempt = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTemplateAboveEditPreview() {
    AppTheme {
        CreateTemplateAbove(
            isLoading = false,
            isEditMode = true,
            onBackAttempt = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTemplateAboveLoadingPreview() {
    AppTheme {
        CreateTemplateAbove(
            isLoading = true,
            isEditMode = false,
            onBackAttempt = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}
