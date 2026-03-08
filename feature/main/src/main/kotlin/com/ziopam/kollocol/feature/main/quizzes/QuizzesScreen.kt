package com.ziopam.kollocol.feature.main.quizzes

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
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.MainScaffoldPreview

@Composable
fun QuizzesScreen(
    viewModel: QuizzesViewModel = hiltViewModel(),
    onQuizClick: (QuizInfo) -> Unit = {}
) {
    val state by viewModel.myQuizzesState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    QuizzesScreen(
        runningQuizzes = state.runningQuizzes,
        pendingQuizzes = state.pendingQuizzes,
        reviewedQuizzes = state.reviewedQuizzes,
        searchQuery = state.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onQuizClick = onQuizClick
    )
}

@Composable
private fun QuizzesScreen(
    runningQuizzes: List<QuizInfo>,
    pendingQuizzes: List<QuizInfo>,
    reviewedQuizzes: List<QuizInfo>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onQuizClick: (QuizInfo) -> Unit
) {
    LayoutWithLargeBottomCard(
        contentAbove = { QuizzesAbove(searchQuery, onSearchQueryChange) },
        content = { MyQuizzesBelow(runningQuizzes, pendingQuizzes, reviewedQuizzes, onQuizClick) }
    )
}

@PreviewLightDark
@Composable
private fun QuizzesScreenPreview() {
    var text by remember { mutableStateOf("") }

    MainScaffoldPreview {
        QuizzesScreen(
            runningQuizzes = emptyList(),
            pendingQuizzes = quizzesInfoExample,
            reviewedQuizzes = emptyList(),
            searchQuery = text,
            onSearchQueryChange = { text = it },
            onQuizClick = {}
        )
    }
}