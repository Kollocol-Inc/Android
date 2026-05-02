package com.ziopam.kollocol.feature.quizgame.playing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.AnswerOptionStats
import com.ziopam.kollocol.domain.model.GameQuestion
import com.ziopam.kollocol.domain.model.QuestionType
import com.ziopam.kollocol.feature.quizgame.GamePhase
import com.ziopam.kollocol.feature.quizgame.R
import com.ziopam.kollocol.feature.quizgame.preview.GamePreviewData
import com.ziopam.kollocol.core.ui.R as CoreUiR

@Composable
internal fun QuestionCard(
    question: GameQuestion,
    playing: GamePhase.Playing,
    onSelectAnswer: (Int) -> Unit,
    onSetOpenAnswer: (String) -> Unit
) {
    val statsMap: Map<String, Int> = playing.answerOptionStats.associate { it.option to it.count }
    val totalVotes = statsMap.values.sum()
    val showStats = playing.isCreator || playing.showStats

    Column {
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineMedium.copy(
                textAlign = TextAlign.Justify,
                hyphens = Hyphens.Auto,
                lineBreak = LineBreak.Paragraph
            ),
            modifier = Modifier.padding(horizontal = 6.dp).padding(bottom = 16.dp),
        )

        when (question.type) {
            QuestionType.OPEN -> {
                if (!playing.isCreator) {
                    OutlinedTextField(
                        value = playing.openAnswer,
                        onValueChange = onSetOpenAnswer,
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        enabled = !playing.hasAnswered && !playing.timeExpired,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.game_enter_answer),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,

                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }

            QuestionType.SINGLE -> {
                question.options?.forEachIndexed { index, option ->
                    OptionRow(
                        text = option,
                        isSelected = index in playing.selectedAnswers,
                        isRadio = true,
                        showStats = showStats,
                        count = statsMap[option] ?: 0,
                        totalVotes = totalVotes,
                        enabled = !playing.hasAnswered,
                        onClick = { onSelectAnswer(index) }
                    )
                }
            }

            QuestionType.MULTIPLE -> {
                question.options?.forEachIndexed { index, option ->
                    OptionRow(
                        text = option,
                        isSelected = index in playing.selectedAnswers,
                        isRadio = false,
                        showStats = showStats,
                        count = statsMap[option] ?: 0,
                        totalVotes = totalVotes,
                        enabled = !playing.hasAnswered,
                        onClick = { onSelectAnswer(index) }
                    )
                }
            }
        }

        Spacer(Modifier.height(5.dp))
    }

}

@Composable
private fun OptionRow(
    text: String,
    isSelected: Boolean,
    isRadio: Boolean,
    showStats: Boolean,
    count: Int,
    totalVotes: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val fraction = if (showStats && totalVotes > 0) count.toFloat() / totalVotes else 0f
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 400),
        label = "optionProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (!showStats) Modifier.clickable(enabled = enabled) { onClick() }
                    else Modifier
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRadio) {
                RadioButton(
                    selected = isSelected,
                    onClick = if (!showStats && enabled) onClick else { {} }
                )
            } else {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = if (!showStats && enabled) { _ -> onClick() } else { {} }
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )

            if (showStats) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(CoreUiR.drawable.groups_filled),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showStats) {
            Spacer(Modifier.height(2.dp))
            LinearProgressIndicator(
                progress = { animatedFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent,
                drawStopIndicator = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SingleQuestionPreview() {
    AppTheme {
        QuestionCard(
            GamePreviewData.question,
            GamePhase.Playing(
                timeRemainingMs = 77000
            ),
            {}, {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MultipleQuestionPreview() {
    AppTheme {
        QuestionCard(
            GamePreviewData.question.copy(
                type = QuestionType.MULTIPLE
            ),
            GamePhase.Playing(
                timeRemainingMs = 77000
            ),
            {}, {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OpenQuestionPreview() {
    AppTheme {
        var text by remember { mutableStateOf("") }

        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            QuestionCard(
                GamePreviewData.question.copy(
                    type = QuestionType.OPEN
                ),
                GamePhase.Playing(
                    openAnswer = text
                ),
                {}, { text = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SingleQuestionPreviewWithStats() {
    AppTheme {
        QuestionCard(
            GamePreviewData.question.copy(
                type = QuestionType.SINGLE
            ),
            GamePhase.Playing(
                selectedAnswers = setOf(3),
                showStats = true
            ),
            {}, {}
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun MultipleQuestionPreviewWithStats() {
    AppTheme {
        QuestionCard(
            GamePreviewData.question.copy(
                type = QuestionType.MULTIPLE
            ),
            GamePhase.Playing(
                selectedAnswers = setOf(0, 1),
                showStats = true,
                answerOptionStats = listOf(
                    AnswerOptionStats("Очев", 20),
                    AnswerOptionStats("Несложно", 80)
                )
            ),
            {}, {}
        )
    }
}