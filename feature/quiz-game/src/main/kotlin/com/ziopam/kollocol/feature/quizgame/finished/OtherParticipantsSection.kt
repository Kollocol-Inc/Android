package com.ziopam.kollocol.feature.quizgame.finished

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import com.ziopam.kollocol.feature.quizgame.LeaderboardRow
import com.ziopam.kollocol.feature.quizgame.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun OtherParticipantsSection(
    participants: List<LeaderboardEntry>,
    sectionAlpha: Float,
    itemAlphas: List<Animatable<Float, AnimationVector1D>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.alpha(sectionAlpha)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.game_other_participants),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = participants.size.toString(),
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

        participants.forEachIndexed { index, entry ->
            val itemAlpha = itemAlphas.getOrNull(index)?.value ?: 1f
            Column(modifier = Modifier.alpha(itemAlpha)) {
                LeaderboardRow(entry = entry)
                if (index < participants.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
