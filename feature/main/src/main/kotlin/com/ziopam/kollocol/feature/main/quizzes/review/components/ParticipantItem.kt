package com.ziopam.kollocol.feature.main.quizzes.review.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.domain.model.QuizParticipant
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun ParticipantItem(
    participant: QuizParticipant,
    onClick: (() -> Unit)?
) {
    val isClickable = onClick != null
    val alpha = if (participant.sessionStatus == "finished") 1f else 0.5f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick?.invoke() } else Modifier)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .alpha(alpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (participant.sessionStatus == "finished" || participant.sessionStatus == "in_progress") {
            val badgeIcon = if (participant.sessionStatus == "finished" && participant.reviewStatus == "reviewed") CoreR.drawable.check else CoreR.drawable.clock_filled
            val badgeColor = if (participant.sessionStatus == "finished" && participant.reviewStatus == "reviewed") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            Icon(
                imageVector = ImageVector.vectorResource(badgeIcon),
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        AvatarPicker(
            avatarUrl = participant.avatarUrl,
            onClick = onClick ?: {},
            modifier = Modifier.size(44.dp),
            defaultIconSize = 24.dp,
            borderColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${participant.firstName} ${participant.lastName}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = participant.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isClickable) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = ImageVector.vectorResource(CoreR.drawable.chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
