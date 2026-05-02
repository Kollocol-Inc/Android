package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.common.TimeFormatter
import com.ziopam.kollocol.core.ui.animations.ExpandedAppearance
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.input.RoundedFocusTextField
import com.ziopam.kollocol.core.ui.input.SearchBar
import com.ziopam.kollocol.core.ui.input.Switch
import com.ziopam.kollocol.core.ui.other.QuizInfoIcon
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.QuizMode
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

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
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val displayedQuestions = if (isSearchVisible && searchQuery.isNotEmpty())
        questions.mapIndexed { i, q -> i to q }.filter { (_, q) -> q.text.contains(searchQuery, ignoreCase = true) }
    else
        questions.mapIndexed { i, q -> i to q }

    val totalPoints = questions.sumOf { it.maxScore }
    val totalTime = questions.sumOf { it.timeLimitSec }

    val focusManager = LocalFocusManager.current

    val bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.template_name),
            style = MaterialTheme.typography.headlineMedium
        )

        RoundedFocusTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = "",
            imeAction = ImeAction.Done,
            textStyle = bodySmall.copy(fontSize = 14.sp),
            modifier = Modifier.fillMaxWidth().height(49.dp),
            onImeAction = { focusManager.clearFocus() }
        )

        Text(
            text = stringResource(R.string.parameters),
            style = MaterialTheme.typography.headlineMedium
        )

        Column (
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.quiz_type),
                    style = bodySmall
                )

                Spacer(modifier = Modifier.weight(1f))

                QuizInfoIcon(
                    quizMode = if (quizType == "async") QuizMode.ASYNC else QuizMode.SYNC,
                    size = 20.dp
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = if (quizType == "async")
                        stringResource(R.string.async_short)
                    else
                        stringResource(R.string.sync_short),
                    style = bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickableNoIndication { onQuizTypeToggle() }
                )
            }

            ExpandedAppearance(
                visible = quizType == "async",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.random_order),
                        style = bodySmall
                    )
                    Switch(
                        isChecked = randomOrder,
                        onCheckedChange = { onRandomOrderToggle() },
                        modifier = Modifier.padding(vertical = 0.dp)
                    )
                }
            }
        }

        HorizontalDivider(modifier =
            if (quizType == "sync") Modifier.padding(vertical = 5.dp) else Modifier
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextWithIcon(
                text = questions.size.toString(),
                iconResource = CoreR.drawable.question_in_circle_filled,
            )

            Text(
                text = " •  $totalPoints ${stringResource(R.string.pts_short)}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.width(5.dp))

            TextWithIcon(
                text = " •  ${TimeFormatter.formatTime(totalTime)}",
                iconResource = CoreR.drawable.clock_filled,
            )

            Spacer(modifier = Modifier.weight(1f))

            CircleIconButton(
                onClick = { isSearchVisible = !isSearchVisible },
                icon = ImageVector.vectorResource(CoreR.drawable.search),
                contentDescription = stringResource(CoreR.string.search),
                size = 40.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            CircleIconButton(
                onClick = onAddQuestionClick,
                icon = ImageVector.vectorResource(CoreR.drawable.plus),
                contentDescription = stringResource(R.string.add_question),
                size = 40.dp
            )
        }

        ExpandedAppearance(visible = isSearchVisible) {
            SearchBar(
                text = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        displayedQuestions.forEach { (originalIndex, question) ->
            QuestionCard(
                index = originalIndex,
                question = question,
                onEditClick = { onEditQuestion(originalIndex) },
                onDeleteClick = { onDeleteQuestion(originalIndex) }
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

@Composable
private fun TextWithIcon(
    text: String,
    iconResource: Int
) {
    val padding = 5.dp

    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
    )

    Spacer(modifier = Modifier.width(padding))

    Icon(
        painter = painterResource(iconResource),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
    )

    Spacer(modifier = Modifier.width(padding))
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