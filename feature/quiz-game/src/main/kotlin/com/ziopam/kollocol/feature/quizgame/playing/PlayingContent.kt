package com.ziopam.kollocol.feature.quizgame.playing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.domain.model.QuestionType
import com.ziopam.kollocol.feature.quizgame.GameHeader
import com.ziopam.kollocol.feature.quizgame.GamePhase
import com.ziopam.kollocol.feature.quizgame.LeaderboardHeader
import com.ziopam.kollocol.feature.quizgame.LeaderboardRow
import com.ziopam.kollocol.feature.quizgame.R
import com.ziopam.kollocol.feature.quizgame.preview.GamePreviewData

@Composable
internal fun PlayingContent(
    quizName: String,
    totalParticipants: Int,
    playing: GamePhase.Playing,
    onSelectAnswer: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onSetOpenAnswer: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    if (!playing.isCreator && playing.hasAnswered && !playing.showStats) {
        WaitingAnsweredContent(
            answeredCount = playing.answerProgress?.participantsAnswered ?: 0,
            totalParticipants = totalParticipants,
            timeRemainingMs = playing.timeRemainingMs
        )
        return
    }

    val question = playing.question

    LayoutWithLargeBottomCard(
        scrollable = false,
        contentAbove = { GameHeader(quizName = quizName, onExit = onNavigateBack) },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            question?.let {
                QuestionHeader(it, playing)
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                if (question != null) {
                    item {
                        QuestionCard(
                            question = question,
                            playing = playing,
                            onSelectAnswer = onSelectAnswer,
                            onSetOpenAnswer = onSetOpenAnswer
                        )
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                if (playing.showStats && playing.leaderboard.isNotEmpty()) {
                    item {
                        HorizontalDivider()
                        Spacer(Modifier.height(15.dp))
                        LeaderboardHeader(playing.leaderboard.size)
                    }

                    items(playing.leaderboard, { it.userId }) { leaderboardItem ->
                        LeaderboardRow(leaderboardItem, modifier = Modifier.animateItem())
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (playing.isCreator) {
                DefaultButton(
                    text = stringResource(R.string.game_next_question),
                    onClick = onNextQuestion,
                    isButtonEnabled = playing.canContinue,
                    isWidthLimited = false,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                if (!playing.hasAnswered && !playing.timeExpired) {
                    val canSubmit = when (question?.type) {
                        QuestionType.SINGLE -> playing.selectedAnswers.isNotEmpty()
                        QuestionType.MULTIPLE -> playing.selectedAnswers.isNotEmpty()
                        QuestionType.OPEN -> playing.openAnswer.isNotBlank()
                        null -> false
                    }
                    DefaultButton(
                        text = stringResource(R.string.game_submit_answer),
                        onClick = onSubmitAnswer,
                        isButtonEnabled = canSubmit,
                        isWidthLimited = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    DefaultButton(
                        text = stringResource(R.string.game_waiting_next_question),
                        onClick = {},
                        isButtonEnabled = false,
                        isWidthLimited = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PlayingContentPreview() {
    AppPreview {
        PlayingContent(
            "Hello World",
            10,
            GamePhase.Playing(
                selectedAnswers = setOf(0),
                question = GamePreviewData.question
            ),
            {}, {}, {}, {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PlayingStatsPreview() {
    AppPreview {
        PlayingContent(
            "Hello World",
            10,
            GamePhase.Playing(
                question = GamePreviewData.question,
                leaderboard = GamePreviewData.leaderboardEntries,
                timeRemainingMs = 10000,
                showStats = true
            ),
            {}, {}, {}, {}
        )
    }
}
