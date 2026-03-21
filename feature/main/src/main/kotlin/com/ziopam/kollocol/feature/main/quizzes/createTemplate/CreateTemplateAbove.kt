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
import com.ziopam.kollocol.core.ui.contentPadding
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun CreateTemplateAbove(
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onSaveClick: () -> Unit
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
            onClick = onNavigateBack,
            icon = ImageVector.vectorResource(CoreR.drawable.arrow_back),
            contentDescription = null
        )

        Text(
            text = stringResource(R.string.template_creation),
            style = MaterialTheme.typography.headlineMedium
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(end = 12.dp).size(24.dp))
        } else {
            CircleIconButton(
                onClick = onSaveClick,
                icon = ImageVector.vectorResource(CoreR.drawable.check),
                contentDescription = null
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
            onNavigateBack = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTemplateAbovePreviewLoading() {
    AppTheme {
        CreateTemplateAbove(
            isLoading = true,
            onNavigateBack = {},
            onSaveClick = {}
        )
    }
}