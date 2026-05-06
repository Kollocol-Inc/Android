package com.ziopam.kollocol.feature.main.quizzes.review.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.contentPadding
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun ReviewAbove(
    title: String,
    isPublishing: Boolean,
    isPublished: Boolean,
    onBackClick: () -> Unit,
    onPublishClick: () -> Unit
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
            onClick = onBackClick,
            icon = ImageVector.vectorResource(CoreR.drawable.arrow_back),
            contentDescription = stringResource(CoreR.string.back),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = title.ifBlank { stringResource(R.string.quiz_review_title) },
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        if (isPublishing) {
            CircularProgressIndicator(modifier = Modifier.padding(end = 12.dp).size(24.dp))
        } else {
            CircleIconButton(
                onClick = onPublishClick,
                icon = ImageVector.vectorResource(CoreR.drawable.send),
                contentDescription = stringResource(R.string.publish_results),
                tint = if (isPublished) ExtraColors.affirmative else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
