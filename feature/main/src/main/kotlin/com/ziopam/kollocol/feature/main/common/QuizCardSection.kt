package com.ziopam.kollocol.feature.main.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.cards.QuizCardList
import com.ziopam.kollocol.domain.model.QuizInfo

@Composable
internal fun QuizCardSection(
    headlineText: String,
    quizzesMissingText: String,
    quizzes: List<QuizInfo>,
    onQuizClick: (QuizInfo) -> Unit
) {
    Text(
        text = headlineText,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
    )

    if (quizzes.isEmpty()) {
        Text(
            text = quizzesMissingText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
        )
    } else {
        QuizCardList(quizzes, onQuizClick)
    }
}