package com.ziopam.kollocol.feature.main.quizzes.review.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.domain.model.ReviewAnswer
import com.ziopam.kollocol.domain.model.ReviewQuestion
import com.ziopam.kollocol.feature.main.R

@Composable
internal fun QuestionAnswerContent(
    question: ReviewQuestion,
    answer: ReviewAnswer?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineMedium.copy(
                textAlign = TextAlign.Justify,
                hyphens = Hyphens.Auto,
                lineBreak = LineBreak.Paragraph
            ),
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .padding(bottom = 16.dp)
        )

        when (question.type) {
            "single" -> SingleChoiceAnswer(question, answer)
            "multiple" -> MultipleChoiceAnswer(question, answer)
            "open" -> OpenAnswer(question, answer)
        }
    }
}

@Composable
private fun SingleChoiceAnswer(question: ReviewQuestion, answer: ReviewAnswer?) {
    val options = question.options ?: return
    val userSelectedIndex = answer?.answer?.trim()?.toIntOrNull()
    val correctIndex = question.correctAnswer.trim().toIntOrNull()

    options.forEachIndexed { index, option ->
        val isUserSelected = userSelectedIndex == index
        val isCorrect = correctIndex == index
        val textColor = when {
            isCorrect -> ExtraColors.affirmative
            isUserSelected -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
        ReviewOptionRow(isRadio = true, isSelected = isUserSelected, textColor = textColor, option = option,
            selectedColor = if (isCorrect) ExtraColors.affirmative else MaterialTheme.colorScheme.error,
            unselectedColor = if (isCorrect) ExtraColors.affirmative else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun MultipleChoiceAnswer(question: ReviewQuestion, answer: ReviewAnswer?) {
    val options = question.options ?: return
    val correctIndices = runCatching {
        question.correctAnswer.trim().removeSurrounding("[", "]")
            .split(",").map { it.trim().toInt() }.toSet()
    }.getOrDefault(emptySet())

    val userIndices = runCatching {
        answer?.answer?.trim()?.removeSurrounding("[", "]")
            ?.split(",")?.map { it.trim().toInt() }?.toSet() ?: emptySet()
    }.getOrDefault(emptySet())

    options.forEachIndexed { index, option ->
        val isUserSelected = userIndices.contains(index)
        val isCorrect = correctIndices.contains(index)
        val textColor = when {
            isCorrect -> ExtraColors.affirmative
            isUserSelected && !isCorrect -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
        ReviewOptionRow(isRadio = false, isSelected = isUserSelected, textColor = textColor, option = option,
            selectedColor = if (isCorrect) ExtraColors.affirmative else MaterialTheme.colorScheme.error,
            unselectedColor = if (isCorrect) ExtraColors.affirmative else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ReviewOptionRow(
    isRadio: Boolean,
    isSelected: Boolean,
    textColor: androidx.compose.ui.graphics.Color,
    option: String,
    selectedColor: androidx.compose.ui.graphics.Color,
    unselectedColor: androidx.compose.ui.graphics.Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRadio) {
                RadioButton(
                    selected = isSelected,
                    onClick = {},
                    colors = RadioButtonDefaults.colors(
                        selectedColor = selectedColor,
                        unselectedColor = unselectedColor
                    )
                )
            } else {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(
                        checkedColor = selectedColor,
                        uncheckedColor = unselectedColor,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
            Text(
                text = option,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun OpenAnswer(question: ReviewQuestion, answer: ReviewAnswer?) {
    val userAnswer = answer?.answer.orEmpty()
    val correctAnswer = question.correctAnswer

    AnswerBox(
        label = stringResource(R.string.participant_answer_label),
        text = userAnswer.ifBlank { stringResource(R.string.no_answer) }
    )

    if (correctAnswer.isNotBlank()) {
        Spacer(modifier = Modifier.height(15.dp))
        AnswerBox(
            label = stringResource(R.string.correct_answer_label),
            text = correctAnswer
        )
    }
}

@Composable
private fun AnswerBox(label: String, text: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
