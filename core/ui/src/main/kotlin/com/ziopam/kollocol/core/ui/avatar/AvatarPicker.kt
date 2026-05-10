package com.ziopam.kollocol.core.ui.avatar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ziopam.kollocol.core.ui.R

@Composable
fun AvatarPicker(
    avatarUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    defaultIconSize: Dp = 50.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    defaultIcon: Int = R.drawable.user,
    onLongClick: (() -> Unit)? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = borderColor.copy(alpha = 0.55f),
                    shape = CircleShape
                )
                .then(
                    if (onLongClick != null) {
                        Modifier.combinedClickable(
                            onClick = onClick,
                            onLongClick = onLongClick
                        )
                    } else {
                        Modifier.clickable(onClick = onClick)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.avatar),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(defaultIcon),
                    contentDescription = stringResource(R.string.pick_a_photo),
                    modifier = Modifier.size(defaultIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        overlay?.invoke(this)
    }
}