package com.ziopam.kollocol.feature.main.quizzes

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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.input.RoundedFocusTextField
import com.ziopam.kollocol.domain.model.QuizMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.ziopam.kollocol.core.ui.R as CoreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartQuizSheet(
    state: StartQuizSheetState,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDeadlineDateChange: (Long) -> Unit,
    onDeadlineTimeChange: (Int, Int) -> Unit,
    onStartClick: () -> Unit
) {
    val template = state.template ?: return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val nowMillis = System.currentTimeMillis()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.deadlineDateMillis ?: nowMillis,
        selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val todayStart = Instant.now()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                    .toEpochMilli()
                return utcTimeMillis >= todayStart
            }
        }
    )

    val timePickerState = rememberTimePickerState(
        initialHour = state.deadlineHour ?: 12,
        initialMinute = state.deadlineMinute ?: 0,
        is24Hour = true
    )

    val bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDeadlineDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        BasicAlertDialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = {
                            onDeadlineTimeChange(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    text = "Запуск квиза",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.size(48.dp))
            }

            Text(
                text = "Название",
                style = MaterialTheme.typography.headlineMedium
            )

            RoundedFocusTextField(
                value = state.title,
                onValueChange = onTitleChange,
                placeholder = "",
                imeAction = ImeAction.Done,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(49.dp),
                onImeAction = { focusManager.clearFocus() }
            )

            Text(
                text = "Параметры",
                style = MaterialTheme.typography.headlineMedium
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (template.mode == QuizMode.ASYNC) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Дедлайн", style = bodySmall)
                        Spacer(Modifier.weight(1f))
                        val dateLabel = state.deadlineDateMillis?.let {
                            Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ru")))
                        } ?: "Дата"
                        AssistChip(
                            onClick = { showDatePicker = true },
                            label = { Text(dateLabel) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            border = null
                        )
                        Spacer(Modifier.width(8.dp))
                        val timeLabel = if (state.deadlineHour != null && state.deadlineMinute != null) {
                            "%02d:%02d".format(state.deadlineHour, state.deadlineMinute)
                        } else "Время"
                        AssistChip(
                            onClick = { showTimePicker = true },
                            label = { Text(timeLabel) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            border = null
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Группа", style = bodySmall)
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Без группы",
                        style = bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickableNoIndication { }
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(CoreR.drawable.chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            DefaultButton(
                text = "Запустить",
                onClick = onStartClick,
                isButtonEnabled = !state.isLoading,
                isWidthLimited = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
