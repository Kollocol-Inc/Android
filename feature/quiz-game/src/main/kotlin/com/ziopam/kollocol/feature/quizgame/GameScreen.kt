package com.ziopam.kollocol.feature.quizgame

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.common.TimeFormatter
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.domain.model.GameFinishResult
import com.ziopam.kollocol.feature.quizgame.finished.AsyncFinishedContent
import com.ziopam.kollocol.feature.quizgame.finished.FinishedContent
import com.ziopam.kollocol.feature.quizgame.playing.PlayingContent
import com.ziopam.kollocol.feature.quizgame.preview.GamePreviewData

@Composable
fun GameScreen(
    onNavigateBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameUiEvent.ShowToast -> {
                    val text = when (val msg = event.message) {
                        is UiText.StringRes -> context.getString(msg.resId, *msg.args.toTypedArray())
                        is UiText.Dynamic -> msg.value
                    }
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                }
                is GameUiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    GameScreen(
        state = state,
        onStartQuiz = viewModel::startQuiz,
        onStartAsyncQuiz = viewModel::startAsyncQuiz,
        onSelectAnswer = viewModel::selectAnswer,
        onSubmitAnswer = viewModel::submitAnswer,
        onSetOpenAnswer = viewModel::setOpenAnswer,
        onNextQuestion = viewModel::nextQuestion,
        onNavigateBack = viewModel::navigateBack,
        onKickParticipant = viewModel::kickParticipant,
        onRetry = viewModel::retryConnect
    )
}

@Composable
fun GameScreen(
    state: GameUiState,
    onStartQuiz: () -> Unit = {},
    onStartAsyncQuiz: () -> Unit = {},
    onSelectAnswer: (Int) -> Unit = {},
    onSubmitAnswer: () -> Unit = {},
    onSetOpenAnswer: (String) -> Unit = {},
    onNextQuestion: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onKickParticipant: (String) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (val phase = state.phase) {
            is GamePhase.Connecting -> ConnectingContent()
            is GamePhase.AsyncInfo -> AsyncInfoContent(
                quizName = state.quizName,
                accessCode = state.accessCode,
                deadline = TimeFormatter.formatDeadline(state.quizDeadline),
                onStart = onStartAsyncQuiz,
                onNavigateBack = onNavigateBack
            )
            is GamePhase.Lobby -> LobbyContent(
                quizName = state.quizName,
                accessCode = state.accessCode,
                lobby = phase,
                selfUserId = state.selfUserId,
                onStartQuiz = onStartQuiz,
                onCancelQuiz = onNavigateBack,
                onExitQuiz = onNavigateBack,
                onKickParticipant = onKickParticipant
            )
            is GamePhase.Playing -> PlayingContent(
                quizName = state.quizName,
                totalParticipants = state.totalParticipants,
                playing = phase,
                isReconnecting = state.isReconnecting,
                isAsync = state.isAsync,
                onSelectAnswer = onSelectAnswer,
                onSubmitAnswer = onSubmitAnswer,
                onSetOpenAnswer = onSetOpenAnswer,
                onNextQuestion = onNextQuestion,
                onNavigateBack = onNavigateBack
            )
            is GamePhase.Finished -> if (state.isAsync) {
                AsyncFinishedContent(
                    quizName = state.quizName,
                    onNavigateBack = onNavigateBack
                )
            } else {
                FinishedContent(
                    finished = phase,
                    quizName = state.quizName,
                    onNavigateBack = onNavigateBack
                )
            }
            is GamePhase.Error -> ErrorContent(
                message = phase.message,
                onRetry = onRetry,
                onNavigateBack = onNavigateBack
            )
        }

        AnimatedVisibility(
            visible = state.isReconnecting,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding())
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f))
                    .alpha(0.8f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.game_reconnecting),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun GameLobbyCreatorPreview() {
    AppPreview {
        GameScreen(
            state = GameUiState(
                phase = GamePhase.Lobby(
                    participants = GamePreviewData.gameParticipants,
                    participantCount = 3,
                    isCreator = true
                ),
                quizName = "Коллоквиум iOS",
                accessCode = "127287"
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun GameLobbyParticipantPreview() {
    AppPreview {
        GameScreen(
            state = GameUiState(
                phase = GamePhase.Lobby(
                    participants = GamePreviewData.gameParticipants,
                    participantCount = GamePreviewData.gameParticipants.size,
                    isCreator = false
                ),
                quizName = "Коллоквиум iOS",
                accessCode = "127287"
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun GamePlayingPreview() {
    AppPreview {
        GameScreen(
            state = GameUiState(
                phase = GamePhase.Playing(
                    question = GamePreviewData.question,
                    isCreator = false,
                    leaderboard = GamePreviewData.leaderboardEntries,
                    showStats = false,
                    timeRemainingMs = 25000
                ),
                quizName = "Коллоквиум iOS",
                accessCode = "127287"
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun GameFinishedPreview() {
    AppPreview {
        GameScreen(
            state = GameUiState(
                phase = GamePhase.Finished(
                    result = GameFinishResult(finalScore = 3600, rank = 1),
                    leaderboard = GamePreviewData.leaderboardEntries
                ),
                quizName = "Коллоквиум iOS",
                accessCode = "127287"
            )
        )
    }
}
