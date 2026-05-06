package com.ziopam.kollocol.feature.main.quizzes.review

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.cards.LocalExtraBottomPadding
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.core.ui.preview.previewAnswersMultiple
import com.ziopam.kollocol.core.ui.preview.previewAnswersOpen
import com.ziopam.kollocol.core.ui.preview.previewAnswersSingle
import com.ziopam.kollocol.core.ui.preview.previewQuestionsMultiple
import com.ziopam.kollocol.core.ui.preview.previewQuestionsOpen
import com.ziopam.kollocol.core.ui.preview.previewQuestionsSingle
import com.ziopam.kollocol.core.ui.uiText.asString
import com.ziopam.kollocol.feature.main.quizzes.review.components.ParticipantReviewAbove
import com.ziopam.kollocol.feature.main.quizzes.review.components.QuestionAnswerContent
import com.ziopam.kollocol.feature.main.quizzes.review.components.QuestionChips
import com.ziopam.kollocol.feature.main.quizzes.review.components.ScoreBar

@Composable
fun ParticipantReviewScreen(
    participantName: String,
    participantEmail: String,
    onNavigateBack: () -> Unit,
    viewModel: ParticipantReviewViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ParticipantReviewEvent.ShowError -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                is ParticipantReviewEvent.ShowToast -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                is ParticipantReviewEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    ParticipantReviewScreen(
        state = state,
        participantName = participantName,
        participantEmail = participantEmail,
        onBackClick = onNavigateBack,
        onSelectQuestion = viewModel::onSelectQuestion,
        onScoreDecrement = viewModel::onScoreDecrement,
        onScoreIncrement = viewModel::onScoreIncrement,
        onGrade = viewModel::onGradeClick
    )
}

@Composable
fun ParticipantReviewScreen(
    state: ParticipantReviewState,
    participantName: String,
    participantEmail: String,
    onBackClick: () -> Unit,
    onSelectQuestion: (Int) -> Unit,
    onScoreDecrement: () -> Unit,
    onScoreIncrement: () -> Unit,
    onGrade: () -> Unit
) {
    val density = LocalDensity.current
    var scoreBarHeightPx by remember { mutableIntStateOf(0) }
    val scoreBarHeightDp = with(density) { scoreBarHeightPx.toDp() }

    Box(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalExtraBottomPadding provides scoreBarHeightDp) {
            LayoutWithLargeBottomCard(
                scrollable = false,
                contentAbove = {
                    ParticipantReviewAbove(
                        name = participantName,
                        email = participantEmail,
                        isGrading = state.isGrading,
                        onBackClick = onBackClick
                    )
                }
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (state.questions.isNotEmpty()) {
                            QuestionChips(
                                questions = state.questions,
                                answers = state.answers,
                                currentIndex = state.currentQuestionIndex,
                                onSelectQuestion = onSelectQuestion
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(top = 8.dp, bottom = 8.dp)
                        ) {
                            state.currentQuestion?.let { question ->
                                QuestionAnswerContent(
                                    question = question,
                                    answer = state.currentAnswer
                                )
                            }
                        }
                    }
                }
            }
        }

        state.currentQuestion?.let { question ->
            ScoreBar(
                score = state.currentScore,
                maxScore = question.maxScore,
                isGrading = state.isGrading,
                onDecrement = onScoreDecrement,
                onIncrement = onScoreIncrement,
                onGrade = onGrade,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { scoreBarHeightPx = it.height }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ParticipantReviewPreviewOpen() {
    AppPreview {
        ParticipantReviewScreen(
            state = ParticipantReviewState(
                instanceTitle = "Коллоквиум iOS",
                isLoading = false,
                questions = previewQuestionsOpen,
                answers = previewAnswersOpen,
                currentQuestionIndex = 0,
                currentScore = 1
            ),
            participantName = "Арсений Потякин",
            participantEmail = "example@doordie.app",
            onBackClick = {},
            onSelectQuestion = {},
            onScoreDecrement = {},
            onScoreIncrement = {},
            onGrade = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ParticipantReviewPreviewSingle() {
    AppPreview {
        ParticipantReviewScreen(
            state = ParticipantReviewState(
                instanceTitle = "Коллоквиум iOS",
                isLoading = false,
                questions = previewQuestionsSingle,
                answers = previewAnswersSingle,
                currentQuestionIndex = 0,
                currentScore = 2
            ),
            participantName = "Арсений Потякин",
            participantEmail = "example@doordie.app",
            onBackClick = {},
            onSelectQuestion = {},
            onScoreDecrement = {},
            onScoreIncrement = {},
            onGrade = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ParticipantReviewPreviewMultiple() {
    AppPreview {
        ParticipantReviewScreen(
            state = ParticipantReviewState(
                instanceTitle = "Коллоквиум iOS",
                isLoading = false,
                questions = previewQuestionsMultiple,
                answers = previewAnswersMultiple,
                currentQuestionIndex = 0,
                currentScore = 0
            ),
            participantName = "Арсений Потякин",
            participantEmail = "example@doordie.app",
            onBackClick = {},
            onSelectQuestion = {},
            onScoreDecrement = {},
            onScoreIncrement = {},
            onGrade = {}
        )
    }
}
