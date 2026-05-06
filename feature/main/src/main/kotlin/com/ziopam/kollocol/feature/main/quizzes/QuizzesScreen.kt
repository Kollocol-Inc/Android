package com.ziopam.kollocol.feature.main.quizzes

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.core.ui.preview.templatesInfoExample
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.MainScaffoldPreview

@Composable
fun QuizzesScreen(
    viewModel: QuizzesViewModel = hiltViewModel(),
    onRunningQuizClick: (QuizInfo) -> Unit = {},
    onReviewQuizClick: (QuizInfo) -> Unit = {},
    onCreateTemplateClick: () -> Unit = {},
    onCreateTemplateAiClick: (String) -> Unit = {},
    onEditTemplateClick: (QuizInfo) -> Unit = {}
) {
    val state by viewModel.myQuizzesState.collectAsStateWithLifecycle()
    val templatesState by viewModel.templatesState.collectAsStateWithLifecycle()
    val startQuizState by viewModel.startQuizState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.refreshTemplates()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is QuizzesEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is QuizzesEvent.QuizStarted -> Toast.makeText(context, "Квиз запущен!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (startQuizState.template != null) {
        StartQuizSheet(
            state = startQuizState,
            onDismiss = viewModel::onDismissStartQuiz,
            onTitleChange = viewModel::onStartQuizTitleChange,
            onDeadlineDateChange = viewModel::onStartQuizDeadlineDateChange,
            onDeadlineTimeChange = viewModel::onStartQuizDeadlineTimeChange,
            onStartClick = viewModel::startQuiz
        )
    }

    QuizzesScreen(
        runningQuizzes = state.runningQuizzes,
        pendingQuizzes = state.pendingQuizzes,
        reviewedQuizzes = state.reviewedQuizzes,
        templates = templatesState.templates,
        searchQuery = state.searchQuery,
        selectedTabIndex = state.selectedTabIndex,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onTabSelected = viewModel::onTabSelected,
        onRunningQuizClick = onRunningQuizClick,
        onQuizClick = onReviewQuizClick,
        onTemplateClick = onEditTemplateClick,
        onStartClick = viewModel::onStartClick,
        onCreateTemplateClick = onCreateTemplateClick,
        onCreateTemplateAiClick = onCreateTemplateAiClick
    )
}

@Composable
private fun QuizzesScreen(
    runningQuizzes: List<QuizInfo>,
    pendingQuizzes: List<QuizInfo>,
    reviewedQuizzes: List<QuizInfo>,
    templates: List<QuizInfo>,
    searchQuery: String,
    selectedTabIndex: Int,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onRunningQuizClick: (QuizInfo) -> Unit,
    onQuizClick: (QuizInfo) -> Unit,
    onTemplateClick: (QuizInfo) -> Unit,
    onStartClick: (QuizInfo) -> Unit,
    onCreateTemplateClick: () -> Unit,
    onCreateTemplateAiClick: (String) -> Unit
) {
    LayoutWithLargeBottomCard(
        contentAbove = {
            QuizzesAbove(
                searchString = searchQuery,
                selectedTabIndex = selectedTabIndex,
                onSearchStringChange = onSearchQueryChange,
                onTabSelected = onTabSelected,
                onCreateTemplateClick = onCreateTemplateClick,
                onCreateTemplateAiClick = onCreateTemplateAiClick
            )
        },
        content = {
            Crossfade(
                targetState = selectedTabIndex,
                animationSpec = tween(durationMillis = 300),
                label = "content_crossfade"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> MyQuizzesBelow(
                        runningQuizzes = runningQuizzes,
                        pendingQuizzes = pendingQuizzes,
                        reviewedQuizzes = reviewedQuizzes,
                        onRunningQuizClick = onRunningQuizClick,
                        onQuizClick = onQuizClick
                    )
                    1 -> TemplatesBelow(
                        templates = templates,
                        onTemplateClick = onTemplateClick,
                        onStartClick = onStartClick
                    )
                    else -> MyQuizzesBelow(
                        runningQuizzes = runningQuizzes,
                        pendingQuizzes = pendingQuizzes,
                        reviewedQuizzes = reviewedQuizzes,
                        onRunningQuizClick = onRunningQuizClick,
                        onQuizClick = onQuizClick
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun MyQuizzesPreview() {
    var text by remember { mutableStateOf("") }

    MainScaffoldPreview {
        QuizzesScreen(
            runningQuizzes = emptyList(),
            pendingQuizzes = quizzesInfoExample,
            reviewedQuizzes = emptyList(),
            templates = emptyList(),
            searchQuery = text,
            selectedTabIndex = 0,
            onSearchQueryChange = { text = it },
            onTabSelected = {},
            onRunningQuizClick = {},
            onQuizClick = {},
            onTemplateClick = {},
            onStartClick = {},
            onCreateTemplateClick = {},
            onCreateTemplateAiClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TemplatesPreview() {
    var text by remember { mutableStateOf("") }

    MainScaffoldPreview {
        QuizzesScreen(
            runningQuizzes = emptyList(),
            pendingQuizzes = emptyList(),
            reviewedQuizzes = emptyList(),
            templates = templatesInfoExample,
            searchQuery = text,
            selectedTabIndex = 1,
            onSearchQueryChange = { text = it },
            onTabSelected = {},
            onRunningQuizClick = {},
            onQuizClick = {},
            onTemplateClick = {},
            onStartClick = {},
            onCreateTemplateClick = {},
            onCreateTemplateAiClick = {}
        )
    }
}