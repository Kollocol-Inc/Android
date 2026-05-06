package com.ziopam.kollocol.feature.main.quizzes.review.components

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
import com.ziopam.kollocol.domain.model.QuizParticipant
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun ParticipantSection(
    title: String,
    participants: List<QuizParticipant>,
    onParticipantClick: ((QuizParticipant) -> Unit)?,
    reviewedCount: Int? = null
) {
    if (participants.isEmpty()) return

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
            if (reviewedCount != null) {
                Text(
                    text = reviewedCount.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(CoreR.drawable.check),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "•",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = participants.size.toString(),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = ImageVector.vectorResource(CoreR.drawable.user_filled),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        participants.forEach { participant ->
            ParticipantItem(
                participant = participant,
                onClick = if (onParticipantClick != null) {
                    { onParticipantClick(participant) }
                } else null
            )
        }
    }
}
