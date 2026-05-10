package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.input.RoundedFocusTextField
import com.ziopam.kollocol.core.ui.input.SearchBar
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.Group
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode
import com.ziopam.kollocol.feature.main.R
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
    onStartClick: () -> Unit,
    onGroupDropdownToggle: () -> Unit,
    onGroupDropdownDismiss: () -> Unit,
    onGroupSearchQueryChange: (String) -> Unit,
    onGroupSelected: (Group?) -> Unit,
    onNavigateToGroups: () -> Unit
) {
    val template = state.template ?: return

    val shipShape = RoundedCornerShape(12.dp)

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
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
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
                        TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = {
                            onDeadlineTimeChange(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) { Text(stringResource(R.string.ok)) }
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
                HorizontalDivider(Modifier.weight(0.5f))
                Text(
                    text = stringResource(R.string.start_quiz_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(Modifier.weight(0.5f))
            }

            Text(
                text = stringResource(R.string.template_name),
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
                onImeAction = { focusManager.clearFocus() },
                capitalization = KeyboardCapitalization.Sentences
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
                if (template.mode == QuizMode.ASYNC) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.deadline), style = bodySmall)
                        Spacer(Modifier.weight(1f))
                        val dateLabel = state.deadlineDateMillis?.let {
                            Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale("ru")))
                        } ?: stringResource(R.string.date)
                        AssistChip(
                            onClick = { showDatePicker = true },
                            label = { Text(dateLabel) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = shipShape,
                            border = null
                        )
                        Spacer(Modifier.width(8.dp))
                        val timeLabel = if (state.deadlineHour != null && state.deadlineMinute != null) {
                            "%02d:%02d".format(state.deadlineHour, state.deadlineMinute)
                        } else stringResource(R.string.time)
                        AssistChip(
                            onClick = { showTimePicker = true },
                            label = { Text(timeLabel) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = shipShape,
                            border = null
                        )
                    }
                }

                val chevronAngle by animateFloatAsState(
                    targetValue = if (state.groupDropdownExpanded) -90f else 90f,
                    animationSpec = tween(durationMillis = 300),
                    label = "chevron_angle"
                )

                ExposedDropdownMenuBox(
                    expanded = state.groupDropdownExpanded,
                    onExpandedChange = { expanded ->
                        if (expanded) onGroupDropdownToggle() else onGroupDropdownDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.group), style = bodySmall)
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = state.selectedGroup?.name ?: stringResource(R.string.no_group),
                            style = bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(CoreR.drawable.chevron_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(chevronAngle)
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = state.groupDropdownExpanded,
                        onDismissRequest = onGroupDropdownDismiss,
                        modifier = Modifier.heightIn(max = 320.dp),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = MaterialTheme.colorScheme.background,
                        shadowElevation = 8.dp
                    ) {
                        SearchBar(
                            placeholder = stringResource(R.string.search_groups),
                            text = state.groupSearchQuery,
                            onQueryChange = onGroupSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.no_group)) },
                            onClick = { onGroupSelected(null) }
                        )
                        val filteredGroups = state.ownedGroups.filter {
                            state.groupSearchQuery.isBlank() ||
                                    it.name.contains(state.groupSearchQuery, ignoreCase = true)
                        }
                        filteredGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group.name) },
                                onClick = { onGroupSelected(group) }
                            )
                        }
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.create_group_action),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CoreR.drawable.add),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                onGroupDropdownDismiss()
                                onNavigateToGroups()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            DefaultButton(
                text = stringResource(R.string.launch),
                onClick = onStartClick,
                isButtonEnabled = !state.isLoading,
                isWidthLimited = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun StartQuizSheetPreviewAsync() {
    AppTheme {
        StartQuizSheet(
            state = StartQuizSheetState(
                title = "Тестовый квиз",
                template = QuizInfo(
                    "","","",10, QuizMode.ASYNC, ""
                ),
                deadlineDateMillis = null,
                deadlineHour = null,
                deadlineMinute = null
            ),
            onDismiss = {},
            onTitleChange = {},
            onDeadlineDateChange = {},
            onDeadlineTimeChange = { _, _ -> {} },
            onStartClick = {},
            onGroupDropdownToggle = {},
            onGroupDropdownDismiss = {},
            onGroupSearchQueryChange = {},
            onGroupSelected = {},
            onNavigateToGroups = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun StartQuizSheetPreviewSync() {
    AppTheme {
        StartQuizSheet(
            state = StartQuizSheetState(
                title = "Тестовый квиз",
                template = QuizInfo(
                    "","","",10, QuizMode.SYNC, ""
                ),
                deadlineDateMillis = null,
                deadlineHour = null,
                deadlineMinute = null
            ),
            onDismiss = {},
            onTitleChange = {},
            onDeadlineDateChange = {},
            onDeadlineTimeChange = { _, _ -> {} },
            onStartClick = {},
            onGroupDropdownToggle = {},
            onGroupDropdownDismiss = {},
            onGroupSearchQueryChange = {},
            onGroupSelected = {},
            onNavigateToGroups = {}
        )
    }
}