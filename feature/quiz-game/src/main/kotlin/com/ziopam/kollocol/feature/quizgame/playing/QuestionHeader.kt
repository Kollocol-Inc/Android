package com.ziopam.kollocol.feature.quizgame.playing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.R as CoreR
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.domain.model.GameQuestion
import com.ziopam.kollocol.feature.quizgame.GamePhase
import com.ziopam.kollocol.feature.quizgame.R
import java.util.Locale

@Composable
fun QuestionHeader(
    question: GameQuestion,
    playing: GamePhase.Playing
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Badge(
            text =  if (question.totalQuestions > 0) {
                stringResource(
                    R.string.game_question_format,
                    question.questionIndex + 1,
                    question.totalQuestions
                )
            } else {
                (question.questionIndex + 1).toString()
            },
            leadingIconRes = CoreR.drawable.question_in_circle_filled
        )

        Text(
            text = stringResource(R.string.game_pts_format, question.maxScore),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val secondsLeft = (playing.timeRemainingMs / 1000).coerceAtLeast(0)
        Badge(
            text = String.format(Locale.ROOT, "%02d:%02d", secondsLeft / 60, secondsLeft % 60),
            textColor = if (playing.timeRemainingMs < 5000) ExtraColors.negative else MaterialTheme.colorScheme.onPrimary,
            leadingIconRes = CoreR.drawable.clock_filled
        )
    }
}

@Composable
private fun Badge(
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    leadingIconRes: Int? = null
) =
    Box (
        modifier = Modifier
            .size(92.dp, 42.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIconRes != null) {
                Icon(
                    painter = painterResource(leadingIconRes),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
            }

            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }