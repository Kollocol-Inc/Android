package com.ziopam.kollocol.feature.main.groups.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.domain.model.GroupMember
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun GroupMemberSection(
    title: String,
    members: List<GroupMember>,
    isPending: Boolean = false,
    isOwner: Boolean = false,
    currentUserEmail: String = "",
    onActionClick: ((GroupMember) -> Unit)? = null
) {
    if (members.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = members.size.toString(),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = ImageVector.vectorResource(CoreR.drawable.user_filled),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        members.forEach { member ->
            val isSelf = member.email == currentUserEmail
            GroupMemberItem(
                member = member,
                isPending = isPending,
                isOwner = isOwner,
                onActionClick = if (isOwner && onActionClick != null && !isSelf) {
                    { onActionClick(member) }
                } else null
            )
        }
    }
}
