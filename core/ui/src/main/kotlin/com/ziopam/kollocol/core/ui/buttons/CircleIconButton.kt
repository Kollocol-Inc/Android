package com.ziopam.kollocol.core.ui.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.R
import com.ziopam.kollocol.core.ui.theme.AppTheme

@Composable
fun CircleIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    size: Dp = 48.dp,
    enabled: Boolean = true,
    tint : Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size / 2).then(iconModifier)
            )
        }
    }
}

@Composable
@Preview
private fun CircleIconButtonPreview() {
    AppTheme {
        CircleIconButton(
            onClick = {},
            icon = ImageVector.vectorResource(id = R.drawable.bell),
            contentDescription = null,
        )
    }
}
