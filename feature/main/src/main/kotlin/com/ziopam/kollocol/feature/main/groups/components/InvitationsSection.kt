package com.ziopam.kollocol.feature.main.groups.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.theme.isDarkTheme
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun InvitationsSection(
    invitations: List<Group>,
    displayedInvitations: List<Group>,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onAccept: (Group) -> Unit,
    onDecline: (Group) -> Unit
) {
    if (invitations.isEmpty()) return

    val cardColor = if (isDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.invitations),
                    style = MaterialTheme.typography.headlineSmall
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text(
                            text = invitations.size.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                if (invitations.size > 1) {
                    Spacer(modifier = Modifier.weight(1f).padding(start = 8.dp))
                    Row (
                        modifier = Modifier.clickableNoIndication(true, onToggleExpanded)
                    ) {
                        Text(
                            text = if (expanded) stringResource(R.string.hide) else stringResource(R.string.show_all),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = ImageVector.vectorResource(CoreR.drawable.expand_more),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            displayedInvitations.forEach { invitation ->
                InvitationItem(
                    group = invitation,
                    onAccept = { onAccept(invitation) },
                    onDecline = { onDecline(invitation) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun InvitationItem(
    group: Group,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
            .padding(horizontal = 13.dp, vertical = 10.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = group.memberCount.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = ImageVector.vectorResource(CoreR.drawable.groups_filled),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button (
            onClick = onAccept,
            modifier = Modifier.width(110.dp)
        ) {
            Text(
                text = stringResource(R.string.accept_invite),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFF6D6B6B))
                .clickableNoIndication(true, onDecline),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(CoreR.drawable.close),
                contentDescription = stringResource(R.string.decline_invite),
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
