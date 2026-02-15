package com.ziopam.kollocol.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.MainScaffoldPreview

@Composable
fun HomeScreen(
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
        HomeScreen(
            personName = "Павел Попов",
            participatingQuizzes = quizzesInfoExample,
            runningQuizzes = quizzesInfoExample.reversed(),
            onParticipatingQuizClick = {},
            onRunningQuizClick = {}
        )
    }
}