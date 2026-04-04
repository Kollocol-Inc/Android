package com.ziopam.kollocol.feature.quizgame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard

@Composable
internal fun FinishedContent(
    finished: GamePhase.Finished,
    quizName: String,
    onNavigateBack: () -> Unit
) {
    LayoutWithLargeBottomCard(
        contentAbove = {
            GameHeader(quizName = quizName, onExit = onNavigateBack)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.game_quiz_finished),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            finished.result?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.game_final_score, result.finalScore),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.game_rank, result.rank),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            if (finished.leaderboard.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                LeaderboardHeader(finished.leaderboard.size)
            }

            Spacer(Modifier.height(24.dp))

            DefaultButton(
                text = stringResource(R.string.game_return),
                onClick = onNavigateBack,
                isButtonEnabled = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
