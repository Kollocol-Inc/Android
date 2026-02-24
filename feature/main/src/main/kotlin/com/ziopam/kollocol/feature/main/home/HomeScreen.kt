package com.ziopam.kollocol.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.MainScaffoldPreview


// TODO Исправить цвета
// TODO Сделать кнопку дополнительной информации
// TODO Переделать нижнюю панель
// TODO Сделать ввода кода
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onParticipatingQuizClick: (QuizInfo) -> Unit = {},
    onRunningQuizClick: (QuizInfo) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    HomeScreenContent(
        personName = state.personName,
        participatingQuizzes = state.participatingQuizzes,
        runningQuizzes = state.hostingQuizzes,
        onParticipatingQuizClick = onParticipatingQuizClick,
        onRunningQuizClick = onRunningQuizClick
    )
}

@Composable
private fun HomeScreenContent(
    personName: String,
    participatingQuizzes: List<QuizInfo>,
    runningQuizzes: List<QuizInfo>,
    onParticipatingQuizClick: (QuizInfo) -> Unit,
    onRunningQuizClick: (QuizInfo) -> Unit
) {
    LayoutWithLargeBottomCard(
        contentAbove = { HomeAbove(personName) },
        content = { HomeBelow(participatingQuizzes, runningQuizzes, onParticipatingQuizClick, onRunningQuizClick) }
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    MainScaffoldPreview {
        HomeScreenContent(
            personName = "Павел Попов",
            participatingQuizzes = quizzesInfoExample,
            runningQuizzes = quizzesInfoExample.reversed(),
            onParticipatingQuizClick = {},
            onRunningQuizClick = {}
        )
    }
}