package com.ziopam.kollocol.feature.main.quizzes

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.MainScaffoldPreview

@Composable
fun QuizzesScreen(
    runningQuizzes: List<QuizInfo>,
    pendingQuizzes: List<QuizInfo>,
    reviewedQuizzes: List<QuizInfo>,
    onQuizClick: (QuizInfo) -> Unit
) {
    LayoutWithLargeBottomCard(
        contentAbove = { QuizzesAbove() },
        content  = { MyQuizzesBelow(runningQuizzes, pendingQuizzes, reviewedQuizzes, onQuizClick) }
    )
}

@PreviewLightDark
@Composable
private fun QuizzesScreenPreview() {
    MainScaffoldPreview {
        QuizzesScreen(
            emptyList(),
            quizzesInfoExample,
            emptyList(),
            {}
        )
    }
}