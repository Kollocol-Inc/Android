package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.feature.main.R

@Composable
internal fun CreateTemplateBelow(
    title: String,
    quizType: String,
    randomOrder: Boolean,
    questions: List<QuestionUiModel>,
    isLoading: Boolean,
    onTitleChange: (String) -> Unit,
    onQuizTypeToggle: () -> Unit,
    onRandomOrderToggle: () -> Unit,
    onAddQuestionClick: () -> Unit,
    onEditQuestion: (Int) -> Unit,
    onDeleteQuestion: (Int) -> Unit
) {
    val totalPoints = questions.sumOf { it.maxScore }
    val totalTime = questions.sumOf { it.timeLimitSec }
    val totalMinutes = totalTime / 60
    val totalSeconds = totalTime % 60

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.template_name),
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text(
            text = stringResource(R.string.parameters),
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.quiz_type),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (quizType == "async")
                    stringResource(R.string.async_short)
                else
                    stringResource(R.string.sync_short),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onQuizTypeToggle() }
                    .padding(8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.random_order),
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = randomOrder,
                onCheckedChange = { onRandomOrderToggle() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.questions_count, questions.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.total_points, totalPoints),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(
                    R.string.total_time_label,
                    "${totalMinutes}:${totalSeconds.toString().padStart(2, '0')}"
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        questions.forEachIndexed { index, question ->
            QuestionCard(
                index = index,
                question = question,
                onEditClick = { onEditQuestion(index) },
                onDeleteClick = { onDeleteQuestion(index) }
            )
        }

        Spacer(Modifier.height(4.dp))
        DefaultButton(
            text = stringResource(R.string.add_question),
            onClick = onAddQuestionClick,
            isButtonEnabled = !isLoading,
            isWidthLimited = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTemplateBelowPreview() {
    AppTheme {
        CreateTemplateBelow(
            title = "Test template",
            quizType = "async",
            randomOrder = true,
            questions = listOf(
                QuestionUiModel(
                    text = "Question 1",
                    maxScore = 10,
                    timeLimitSec = 120,
                ),
                QuestionUiModel(
                    text = "Question 2",
                    maxScore = 20,
                    timeLimitSec = 240,
                )
            ),
            isLoading = false,
            onTitleChange = {},
            onQuizTypeToggle = {},
            onRandomOrderToggle = {},
            onAddQuestionClick = {},
            onEditQuestion = {},
            onDeleteQuestion = {}
        )
    }
}