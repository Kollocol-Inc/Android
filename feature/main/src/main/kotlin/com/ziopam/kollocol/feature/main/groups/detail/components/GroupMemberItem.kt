package com.ziopam.kollocol.feature.main.groups.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.domain.model.GroupMember
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun GroupMemberItem(
    member: GroupMember,
    isPending: Boolean = false,
    isOwner: Boolean = false,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .alpha(if (isPending) 0.55f else 1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarPicker(
            avatarUrl = member.avatarUrl,
            onClick = {},
            modifier = Modifier.size(44.dp),
            defaultIconSize = 22.dp,
            borderColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${member.firstName} ${member.lastName}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = member.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isOwner && onActionClick != null) {
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = if (isPending)
                        ImageVector.vectorResource(CoreR.drawable.close)
                    else
                        ImageVector.vectorResource(CoreR.drawable.delete),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color(0xFFE53935)
                )
            }
        }
    }
}
