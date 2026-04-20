package com.ziopam.kollocol.feature.quizgame.finished

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.util.rankToMedalEmoji
import com.ziopam.kollocol.domain.model.LeaderboardEntry

@Composable
internal fun PodiumParticipant(
    entry: LeaderboardEntry,
    isFirst: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = entry.rank.rankToMedalEmoji() ?: "", fontSize = if (isFirst) 28.sp else 22.sp)
        Spacer(Modifier.height(4.dp))
        AvatarPicker(
            avatarUrl = entry.avatarUrl,
            onClick = {},
            modifier = Modifier.size(if (isFirst) 80.dp else 64.dp),
            defaultIconSize = if (isFirst) 44.dp else 34.dp,
            borderColor = when (entry.rank) {
                1 -> Color(0xFFFFD700)
                2 -> Color(0xFFC0C0C0)
                else -> Color(0xFFCD7F32)
            }
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = entry.name.ifBlank { entry.userId },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = if (isFirst) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${entry.score}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}