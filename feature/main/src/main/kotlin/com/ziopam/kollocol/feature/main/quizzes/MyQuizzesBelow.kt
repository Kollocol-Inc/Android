package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.common.QuizCardSection

@Composable
fun MyQuizzesBelow(
    runningQuizzes: List<QuizInfo>,
    pendingQuizzes: List<QuizInfo>,
    reviewedQuizzes: List<QuizInfo>,
    onQuizClick: (QuizInfo) -> Unit
) {

    Column(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuizCardSection(
            headlineText = stringResource(R.string.currently_running),
            quizzesMissingText = stringResource(R.string.no_running_quizzes),
            quizzes = runningQuizzes,
            onQuizClick = onQuizClick
        )

        QuizCardSection(
            headlineText = stringResource(R.string.pending_quizzes),
            quizzesMissingText = stringResource(R.string.no_pending_quizzes),
            quizzes = pendingQuizzes,
            onQuizClick = onQuizClick
        )

        QuizCardSection(
            headlineText = stringResource(R.string.reviewed_quizzes),
            quizzesMissingText = stringResource(R.string.no_reviewed_quizzes),
            quizzes = reviewedQuizzes,
            onQuizClick = onQuizClick
        )
    }
}