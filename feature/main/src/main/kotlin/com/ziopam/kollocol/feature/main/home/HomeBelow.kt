package com.ziopam.kollocol.feature.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.common.QuizCardSection

@Composable
fun HomeBelow(
    participatingQuizzes: List<QuizInfo>,
    runningQuizzes: List<QuizInfo>,
    onParticipatingQuizClick: (QuizInfo) -> Unit,
    onRunningQuizClick: (QuizInfo) -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuizCardSection(
            headlineText = stringResource(R.string.currently_participating),
            quizzesMissingText = stringResource(R.string.no_participating_quizzes),
            quizzes = participatingQuizzes,
            onQuizClick = onParticipatingQuizClick
        )

        QuizCardSection(
            headlineText = stringResource(R.string.currently_running),
            quizzesMissingText = stringResource(R.string.no_running_quizzes),
            quizzes = runningQuizzes,
            onQuizClick = onRunningQuizClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeAbovePreview(){
    AppTheme {
        HomeBelow(
            participatingQuizzes = quizzesInfoExample,
            runningQuizzes = quizzesInfoExample.reversed(),
            onRunningQuizClick = {},
            onParticipatingQuizClick = {}
        )
    }
}