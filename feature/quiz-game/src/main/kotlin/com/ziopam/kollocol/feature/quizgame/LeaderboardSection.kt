package com.ziopam.kollocol.feature.quizgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.util.rankToMedalEmoji
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun LeaderboardHeader(leaderBoardSize: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.game_leaderboard),
                style = MaterialTheme.typography.headlineMedium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = leaderBoardSize.toString(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(CoreR.drawable.user_filled),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
internal fun LeaderboardRow(entry: LeaderboardEntry, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (entry.isAnswered) 1f else 0.6f },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarPicker(
            avatarUrl = entry.avatarUrl,
            onClick = {},
            modifier = Modifier.size(44.dp),
            defaultIconSize = 24.dp,
        ) {
            if (!entry.isOnline) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.wifi_off),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(19.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }

        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name.ifBlank { entry.userId },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = entry.email,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            val medal = entry.rank.rankToMedalEmoji()
            if (medal != null) {
                Text(text = medal, fontSize = 18.sp)
                Spacer(Modifier.width(4.dp))
            }

            Text(
                text = "${entry.score}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
