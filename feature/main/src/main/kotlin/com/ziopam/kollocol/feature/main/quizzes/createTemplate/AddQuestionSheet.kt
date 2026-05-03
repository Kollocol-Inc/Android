package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.dialogs.DefaultDialog
import com.ziopam.kollocol.core.ui.input.RoundedFocusTextField
import com.ziopam.kollocol.core.ui.input.RoundedMultilineTextField
import com.ziopam.kollocol.core.ui.input.Switch
import com.ziopam.kollocol.core.ui.other.SelectiveTabs
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.components.PointsChip
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.components.ScoreInputDialog
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.components.TimeChip
import com.ziopam.kollocol.feature.main.quizzes.createTemplate.components.TimePickerDialog
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
    var correctIndices by remember { mutableStateOf(initialQuestion?.correctOptionIndices ?: emptySet<Int>()) }
    var correctAnswer by remember { mutableStateOf(initialQuestion?.correctAnswer ?: "") }
    var maxScore by remember { mutableIntStateOf(initialQuestion?.maxScore ?: 10) }
    var timeLimitSec by remember { mutableIntStateOf(initialQuestion?.timeLimitSec ?: 30) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showScoreInputDialog by remember { mutableStateOf(false) }

    val questionType = when (selectedTypeIndex) {
        0 -> QuestionType.SINGLE
        1 -> QuestionType.MULTIPLE
        else -> QuestionType.OPEN
    }

    val selectionColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else Color.White

    val errorDialogTitle = stringResource(R.string.error_dialog_title)
    val okText = stringResource(R.string.ok)
    val emptyQuestionError = stringResource(R.string.question_text_empty_error)
    val noOptionsError = stringResource(R.string.no_options_error)
    val noCorrectAnswerError = stringResource(R.string.no_correct_answer_error)
    val blankOptionSelectedError = stringResource(R.string.blank_option_selected_error)
    val cancelText = stringResource(R.string.cancel)

    if (errorText != null) {
        DefaultDialog(
            title = errorDialogTitle,
            message = errorText!!,
            confirmText = okText,
            onConfirm = { errorText = null }
        )
    }

    if (showTimePickerDialog) {
        TimePickerDialog(
            currentSec = timeLimitSec,
            okText = okText,
            cancelText = cancelText,
            timeLabel = stringResource(R.string.time),
            onConfirm = { timeLimitSec = it; showTimePickerDialog = false },
            onDismiss = { showTimePickerDialog = false }
        )
    }

    if (showScoreInputDialog) {
        ScoreInputDialog(
            currentScore = maxScore,
            okText = okText,
            cancelText = cancelText,
            title = stringResource(R.string.points),
            onConfirm = { maxScore = it; showScoreInputDialog = false },
            onDismiss = { showScoreInputDialog = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        containerColor = MaterialTheme.colorScheme.surface,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    onClick = onDismiss,
                    icon = ImageVector.vectorResource(CoreR.drawable.close),
                    contentDescription = stringResource(CoreR.string.back),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.add_question),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                CircleIconButton(
                    onClick = {
                        val nonBlankOptions = options.filter { it.isNotBlank() }
                        val error = when {
                            questionText.isBlank() -> emptyQuestionError
                            questionType != QuestionType.OPEN && nonBlankOptions.isEmpty() -> noOptionsError
                            questionType != QuestionType.OPEN && correctIndices.isEmpty() -> noCorrectAnswerError
                            questionType != QuestionType.OPEN && correctIndices.any { idx -> idx >= options.size || options[idx].isBlank() } -> blankOptionSelectedError
                            else -> null
                        }
                        if (error != null) {
                            errorText = error
                        } else {
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
                        }
                    },
                    icon = ImageVector.vectorResource(CoreR.drawable.check),
                    contentDescription = null,
                    tint = ExtraColors.affirmative
                )
            }

            val tabs = listOf(
                stringResource(R.string.single_choice),
                stringResource(R.string.multiple_choice),
                stringResource(R.string.open_ended)
            )
            SelectiveTabs(
                tabs = tabs,
                selectedIndex = selectedTypeIndex,
                onTabSelected = { index ->
                    if (index != selectedTypeIndex) {
                        correctIndices = if (index != 2) {
                            correctIndices.minOrNull()?.let { setOf(it) } ?: emptySet()
                        } else {
                            emptySet()
                        }
                        selectedTypeIndex = index
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                selectionWidthPadding = 6.dp,
                insideVerticalPadding = 10.dp,
                backGroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                selectionColor = selectionColor
            )

            Text(
                text = stringResource(R.string.question_label),
                style = MaterialTheme.typography.headlineMedium
            )
            RoundedMultilineTextField(
                value = questionText,
                onValueChange = { questionText = it },
                placeholder = "",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Text(
                text = stringResource(R.string.parameters),
                style = MaterialTheme.typography.headlineMedium
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.points), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    PointsChip(
                        value = maxScore,
                        onDecrement = { if (maxScore > 1) maxScore-- },
                        onIncrement = { if (maxScore < 100) maxScore++ },
                        onValueClick = { showScoreInputDialog = true }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.time), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    TimeChip(
                        seconds = timeLimitSec,
                        onClick = { showTimePickerDialog = true }
                    )
                }
            }

            when (questionType) {
                QuestionType.SINGLE, QuestionType.MULTIPLE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.answer_options),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        CircleIconButton(
                            onClick = { options = options + "" },
                            icon = ImageVector.vectorResource(CoreR.drawable.add),
                            contentDescription = null,
                            size = 40.dp
                        )
                    }

                    options.forEachIndexed { idx, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoundedFocusTextField(
                                value = option,
                                onValueChange = { newVal ->
                                    options = options.toMutableList().apply { this[idx] = newVal }
                                },
                                placeholder = stringResource(R.string.option_hint, idx + 1),
                                modifier = Modifier.weight(1f),
                                capitalization = KeyboardCapitalization.Sentences
                            )
                            Switch(
                                isChecked = idx in correctIndices,
                                onCheckedChange = { checked ->
                                    correctIndices = when {
                                        !checked -> correctIndices - idx
                                        questionType == QuestionType.SINGLE -> setOf(idx)
                                        else -> correctIndices + idx
                                    }
                                }
                            )
                        }
                    }
                }
                QuestionType.OPEN -> {
                    Text(
                        text = stringResource(R.string.answer_optional),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    RoundedMultilineTextField(
                        value = correctAnswer,
                        onValueChange = { correctAnswer = it },
                        placeholder = "",
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@PreviewLightDark
@Composable
fun AddQuestionSheetPreview() {
    AppTheme {
        AddQuestionSheet(
            {}, {}
        )
    }
}
