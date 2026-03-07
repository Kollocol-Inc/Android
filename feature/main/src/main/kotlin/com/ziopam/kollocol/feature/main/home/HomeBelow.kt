package com.ziopam.kollocol.feature.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.cards.QuizCardList
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.feature.main.R

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
        Text(
            text = stringResource(R.string.currently_participating),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (participatingQuizzes.isEmpty()) {
            Text(
                text = stringResource(R.string.no_participating_quizzes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            )
        } else {
            QuizCardList(participatingQuizzes, onParticipatingQuizClick)
        }

        Text(
            text = stringResource(R.string.currently_running),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        if (runningQuizzes.isEmpty()) {
            Text(
                text = stringResource(R.string.no_running_quizzes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            )
        } else {
            QuizCardList(runningQuizzes, onRunningQuizClick)
        }
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