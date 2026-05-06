package com.ziopam.kollocol.feature.main.quizzes.review.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.domain.model.ReviewAnswer
import com.ziopam.kollocol.domain.model.ReviewQuestion
import com.ziopam.kollocol.feature.main.R

@Composable
internal fun QuestionChips(
    questions: List<ReviewQuestion>,
    answers: List<ReviewAnswer>,
    currentIndex: Int,
    onSelectQuestion: (Int) -> Unit
) {
    LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
        itemsIndexed(questions) { index, question ->
            val answer = answers.find { it.questionId == question.id }
            val hasAnswer = !answer?.answer.isNullOrBlank()
            val isSelected = index == currentIndex

            val chipShape = RoundedCornerShape(20.dp)

            val borderColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                answer?.isCorrect == true -> ExtraColors.affirmative
                hasAnswer && answer?.isCorrect == false -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline
            }

            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(width = 54.dp, height = 64.dp)
                    .border(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = borderColor,
                        shape = chipShape
                    )
                    .clickableNoIndication { onSelectQuestion(index) },
                shape = chipShape,
                color = if (hasAnswer) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surface
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = "${question.maxScore} ${stringResource(R.string.pts_short)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
