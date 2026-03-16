package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.other.SelectiveTabs
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionSheet(
    onDismiss: () -> Unit,
    onSave: (QuestionUiModel) -> Unit,
    initialQuestion: QuestionUiModel? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var questionText by remember { mutableStateOf(initialQuestion?.text ?: "") }
    var selectedTypeIndex by remember {
        mutableIntStateOf(
            when (initialQuestion?.type) {
                QuestionType.MULTIPLE -> 1
                QuestionType.OPEN -> 2
                else -> 0
            }
        )
    }
    var options by remember { mutableStateOf(initialQuestion?.options ?: listOf("", "", "", "")) }
    var correctIndices by remember { mutableStateOf(initialQuestion?.correctOptionIndices ?: emptySet()) }
    var correctAnswer by remember { mutableStateOf(initialQuestion?.correctAnswer ?: "") }
    var maxScore by remember { mutableIntStateOf(initialQuestion?.maxScore ?: 10) }
    var timeLimitSec by remember { mutableIntStateOf(initialQuestion?.timeLimitSec ?: 30) }

    val questionType = when (selectedTypeIndex) {
        0 -> QuestionType.SINGLE
        1 -> QuestionType.MULTIPLE
        else -> QuestionType.OPEN
    }

    val selectionColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.White

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    onClick = onDismiss,
                    icon = ImageVector.vectorResource(CoreR.drawable.close),
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.add_question),
                    style = MaterialTheme.typography.titleLarge
                )
                CircleIconButton(
                    onClick = {
                        onSave(
                            QuestionUiModel(
                                text = questionText,
                                type = questionType,
                                options = if (questionType != QuestionType.OPEN) options else listOf("", "", "", ""),
                                correctAnswer = if (questionType == QuestionType.OPEN) correctAnswer else "",
                                correctOptionIndices = if (questionType != QuestionType.OPEN) correctIndices else emptySet(),
                                maxScore = maxScore,
                                timeLimitSec = timeLimitSec
                            )
                        )
                    },
                    icon = ImageVector.vectorResource(CoreR.drawable.check),
                    contentDescription = null
                )
            }

            // Question type tabs
            val tabs = listOf(
                stringResource(R.string.single_choice),
                stringResource(R.string.multiple_choice),
                stringResource(R.string.open_ended)
            )
            SelectiveTabs(
                tabs = tabs,
                selectedIndex = selectedTypeIndex,
                onTabSelected = { index ->
                    selectedTypeIndex = index
                    correctIndices = emptySet()
                },
                modifier = Modifier.fillMaxWidth(),
                selectionWidthPadding = 6.dp,
                insideVerticalPadding = 8.dp,
                backGroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                selectionColor = selectionColor
            )

            // Question text
            Text(
                text = stringResource(R.string.question_label),
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2
            )

            // Parameters row
            Text(
                text = stringResource(R.string.parameters),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Points counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.points),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    CounterControl(
                        value = maxScore,
                        onDecrement = { if (maxScore > 1) maxScore-- },
                        onIncrement = { if (maxScore < 100) maxScore++ }
                    )
                }
                // Time counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.time),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(8.dp))
                    CounterControl(
                        value = timeLimitSec,
                        onDecrement = { if (timeLimitSec > 5) timeLimitSec -= 5 },
                        onIncrement = { if (timeLimitSec < 600) timeLimitSec += 5 },
                        suffix = stringResource(R.string.sec_short)
                    )
                }
            }

            // Answer section
            when (questionType) {
                QuestionType.SINGLE, QuestionType.MULTIPLE -> {
                    Text(
                        text = stringResource(R.string.answer_options),
                        style = MaterialTheme.typography.titleMedium
                    )
                    options.forEachIndexed { idx, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (questionType == QuestionType.SINGLE) {
                                RadioButton(
                                    selected = idx in correctIndices,
                                    onClick = { correctIndices = setOf(idx) }
                                )
                            } else {
                                Checkbox(
                                    checked = idx in correctIndices,
                                    onCheckedChange = { checked ->
                                        correctIndices = if (checked) {
                                            correctIndices + idx
                                        } else {
                                            correctIndices - idx
                                        }
                                    }
                                )
                            }
                            OutlinedTextField(
                                value = option,
                                onValueChange = { newVal ->
                                    options = options.toMutableList().apply { this[idx] = newVal }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                placeholder = {
                                    Text(stringResource(R.string.option_hint, idx + 1))
                                },
                                singleLine = true
                            )
                        }
                    }
                    // Add option button
                    IconButton(
                        onClick = { options = options + "" },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CoreR.drawable.add),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                QuestionType.OPEN -> {
                    Text(
                        text = stringResource(R.string.answer_optional),
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = correctAnswer,
                        onValueChange = { correctAnswer = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun CounterControl(
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    suffix: String = ""
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                Text(
                    text = "—",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = if (suffix.isNotEmpty()) "$value $suffix" else "$value",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
