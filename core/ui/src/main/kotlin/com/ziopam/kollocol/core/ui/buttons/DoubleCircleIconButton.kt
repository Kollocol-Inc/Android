package com.ziopam.kollocol.core.ui.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.R
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.core.ui.theme.ExtraColors

@Composable
fun DoubleCircleIconButton(
    onLeftClick: () -> Unit,
    leftIcon: ImageVector,
    leftContentDescription: String?,
    onRightClick: () -> Unit,
    rightIcon: ImageVector,
    rightContentDescription: String?,
    modifier: Modifier = Modifier,
    leftTint: Color = MaterialTheme.colorScheme.primary,
    rightTint: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier
            .width(size * 2)
            .size(size * 2, size),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickableNoIndication(enabled = enabled, onClick = onLeftClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leftIcon,
                    contentDescription = leftContentDescription,
                    tint = leftTint,
                    modifier = Modifier.size(size / 2)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickableNoIndication(enabled = enabled, onClick = onRightClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = rightIcon,
                    contentDescription = rightContentDescription,
                    tint = rightTint,
                    modifier = Modifier.size(size / 2)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DoubleCircleIconButtonPreview() {
    AppTheme {
        DoubleCircleIconButton(
            onLeftClick = {},
            leftIcon = ImageVector.vectorResource(R.drawable.delete),
            leftContentDescription = null,
            leftTint = MaterialTheme.colorScheme.error,
            onRightClick = {},
            rightIcon = ImageVector.vectorResource(R.drawable.check),
            rightContentDescription = null,
            rightTint = ExtraColors.affirmative
        )
    }
}
