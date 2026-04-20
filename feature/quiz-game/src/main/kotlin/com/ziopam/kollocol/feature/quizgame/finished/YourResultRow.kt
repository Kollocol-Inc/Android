package com.ziopam.kollocol.feature.quizgame.finished

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.util.rankToMedalEmoji
import com.ziopam.kollocol.domain.model.GameFinishResult
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import com.ziopam.kollocol.feature.quizgame.R

@Composable
internal fun YourResultRow(
    result: GameFinishResult,
    entry: LeaderboardEntry?,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(cardColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Ваше место:" + rank number in primary color
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.game_your_place_prefix),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${result.rank}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Medal emoji + avatar overlapping
        val medal = result.rank.rankToMedalEmoji()
        if (medal != null && entry != null) {
            Box(modifier = Modifier.size(52.dp, 36.dp)) {
                Text(
                    text = medal,
                    fontSize = 26.sp,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
                AvatarPicker(
                    avatarUrl = entry.avatarUrl,
                    onClick = {},
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopEnd),
                    defaultIconSize = 14.dp
                )
            }
        } else if (entry != null) {
            AvatarPicker(
                avatarUrl = entry.avatarUrl,
                onClick = {},
                modifier = Modifier.size(32.dp),
                defaultIconSize = 16.dp
            )
        }

        Spacer(Modifier.width(12.dp))

        // Score on the right
        Text(
            text = "${result.finalScore}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
