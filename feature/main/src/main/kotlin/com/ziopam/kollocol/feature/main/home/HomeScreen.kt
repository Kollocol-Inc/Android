package com.ziopam.kollocol.feature.main.home

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
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onParticipatingQuizClick: (QuizInfo) -> Unit = {},
    onRunningQuizClick: (QuizInfo) -> Unit = {},
    onJoinQuiz: (String) -> Unit = {}
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
        onRunningQuizClick = onRunningQuizClick,
        code = state.quizCode,
        codeChanged = viewModel::onCodeChanged,
        onJoinQuiz = onJoinQuiz
    )
}

@Composable
private fun HomeScreenContent(
    personName: String,
    code: String,
    codeChanged: (String) -> Unit,
    participatingQuizzes: List<QuizInfo>,
    runningQuizzes: List<QuizInfo>,
    onParticipatingQuizClick: (QuizInfo) -> Unit,
    onRunningQuizClick: (QuizInfo) -> Unit,
    onJoinQuiz: (String) -> Unit = {}
) {
    LayoutWithLargeBottomCard(
        contentAbove = {
            HomeAbove(
                personName = personName,
                code = code,
                onCodeChanged = codeChanged,
                onJoinQuiz = { if (code.length == 6) onJoinQuiz(code) }
            )
        },
        content = { HomeBelow(participatingQuizzes, runningQuizzes, onParticipatingQuizClick, onRunningQuizClick) }
    )
}

@PreviewLightDark
@Composable
private fun HomeScreenPreview() {
    var code by remember { mutableStateOf("") }

    MainScaffoldPreview {
        HomeScreenContent(
            personName = "Павел Попов",
            code = code,
            codeChanged = { code = it },
            participatingQuizzes = quizzesInfoExample,
            runningQuizzes = quizzesInfoExample.reversed(),
            onParticipatingQuizClick = {},
            onRunningQuizClick = {}
        )
    }
}