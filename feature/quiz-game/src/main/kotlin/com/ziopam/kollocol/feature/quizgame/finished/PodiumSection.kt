package com.ziopam.kollocol.feature.quizgame.finished

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import kotlin.math.roundToInt

@Composable
internal fun PodiumSection(
    first: LeaderboardEntry?,
    second: LeaderboardEntry?,
    third: LeaderboardEntry?,
    secondOffsetX: Float,
    secondAlpha: Float,
    thirdOffsetX: Float,
    thirdAlpha: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .alpha(secondAlpha)
                .offset { IntOffset(secondOffsetX.roundToInt(), 0) }
        ) {
            if (second != null) {
                PodiumParticipant(
                    entry = second,
                    isFirst = false,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (first != null) {
                PodiumParticipant(
                    entry = first,
                    isFirst = true,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-12).dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .alpha(thirdAlpha)
                .offset { IntOffset(thirdOffsetX.roundToInt(), 0) }
        ) {
            if (third != null) {
                PodiumParticipant(
                    entry = third,
                    isFirst = false,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
