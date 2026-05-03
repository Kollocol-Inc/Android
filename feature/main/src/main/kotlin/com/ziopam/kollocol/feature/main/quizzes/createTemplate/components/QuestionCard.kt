package com.ziopam.kollocol.feature.main.quizzes.createTemplate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.common.TimeFormatter
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.core.ui.theme.isDarkTheme
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.QuestionType
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.QuestionUiModel
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
fun QuestionCard(
    index: Int,
    question: QuestionUiModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier
) {
    val cardColor = if (isDarkTheme()) MaterialTheme.colorScheme.surfaceVariant
    else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .clickableNoIndication(onClick = onEditClick)
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val typeLabel = when (question.type) {
                        QuestionType.SINGLE -> stringResource(R.string.single_choice)
                        QuestionType.MULTIPLE -> stringResource(R.string.multiple_choice)
                        QuestionType.OPEN -> stringResource(R.string.open_ended)
                    }
                    val metaColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    val metaStyle = MaterialTheme.typography.bodySmall

                    Text(
                        text = String.format("%02d", index + 1),
                        style = metaStyle,
                        color = metaColor
                    )
                    BulletSep()
                    Text(text = typeLabel, style = metaStyle, color = metaColor)
                    BulletSep()
                    Text(
                        text = TimeFormatter.formatTime(question.timeLimitSec),
                        style = metaStyle,
                        color = metaColor
                    )
                    BulletSep()
                    Text(
                        text = "${question.maxScore} ${stringResource(R.string.pts_short)}",
                        style = metaStyle,
                        color = metaColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CoreR.drawable.edit),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CoreR.drawable.delete),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(CoreR.drawable.drag_handle),
                        contentDescription = null,
                        modifier = dragHandleModifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall
            )

            when (question.type) {
                QuestionType.SINGLE, QuestionType.MULTIPLE -> {
                    val validOptions = question.options
                        .mapIndexed { idx, opt -> idx to opt }
                        .filter { (_, opt) -> opt.isNotBlank() }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        validOptions.chunked(2).forEach { chunk ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                chunk.forEach { (idx, option) ->
                                    val isCorrect = idx in question.correctOptionIndices
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (question.type == QuestionType.SINGLE) {
                                            RadioButton(
                                                selected = isCorrect,
                                                onClick = null,
                                                modifier = Modifier.size(20.dp),
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = ExtraColors.affirmative,
                                                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                )
                                            )
                                        } else {
                                            Checkbox(
                                                checked = isCorrect,
                                                onCheckedChange = null,
                                                modifier = Modifier.size(20.dp),
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = ExtraColors.affirmative,
                                                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                )
                                            )
                                        }
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (isCorrect) ExtraColors.affirmative
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                if (chunk.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                QuestionType.OPEN -> {
                    if (question.correctAnswer.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = question.correctAnswer,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BulletSep() {
    Text(
        text = "•",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    )
}
