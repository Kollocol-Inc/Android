package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onQuizClick: (QuizInfo) -> Unit = {},
    onCreateTemplateClick: () -> Unit = {}
) {
    val state by viewModel.myQuizzesState.collectAsStateWithLifecycle()
    val templatesState by viewModel.templatesState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.refreshTemplates()
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
        onQuizClick = onQuizClick,
        onTemplateClick = onQuizClick,
        onStartClick = {},
        onCreateTemplateClick = onCreateTemplateClick
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
    onQuizClick: (QuizInfo) -> Unit,
    onTemplateClick: (QuizInfo) -> Unit,
    onStartClick: (QuizInfo) -> Unit,
    onCreateTemplateClick: () -> Unit = {}
) {
    LayoutWithLargeBottomCard(
        contentAbove = {
            QuizzesAbove(
                searchString = searchQuery,
                selectedTabIndex = selectedTabIndex,
                onSearchStringChange = onSearchQueryChange,
                onTabSelected = onTabSelected,
                onCreateTemplateClick = onCreateTemplateClick
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
            onQuizClick = {},
            onTemplateClick = {},
            onStartClick = {}
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
            onQuizClick = {},
            onTemplateClick = {},
            onStartClick = {}
        )
    }
}