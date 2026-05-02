package com.ziopam.kollocol.feature.auth.personal

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.avatar.rememberAvatarPicker
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.core.ui.uiText.asString
import com.ziopam.kollocol.feature.auth.R

@Composable
fun SignUpAvatar(
    state: PersonalUiState,
    onAvatarSelected: (Uri?) -> Unit,
    onAvatarError: (UiText) -> Unit,
    onAvatarRemove: () -> Unit,
){
    val pickAvatar = rememberAvatarPicker(
        onAvatarSelected = onAvatarSelected,
        onError = onAvatarError
    )
    AvatarPicker(
        avatarUrl = state.avatarUri?.toString(),
        onClick = pickAvatar,
        overlay = {
            if (state.avatarUri != null) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.close),
                    contentDescription = stringResource(R.string.remove_avatar),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(25.dp)
                        .offset(15.dp, (-5).dp)
                        .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                        .clickable(onClick = onAvatarRemove),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        borderColor = if (state.error is PersonalError.Avatar)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 20.dp)
            .size(100.dp)
    )

    if (state.error is PersonalError.Avatar) {
        Text(
            text = state.error.message.asString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )
    }
}