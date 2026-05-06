package com.ziopam.kollocol.feature.main.quizzes.review

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.dialogs.DefaultDialog
import com.ziopam.kollocol.core.ui.input.SearchBar
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.core.ui.preview.quizParticipantReviewExample
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.core.ui.uiText.asString
import com.ziopam.kollocol.domain.model.QuizParticipant
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.quizzes.review.components.ParticipantSection
import com.ziopam.kollocol.feature.main.quizzes.review.components.ReviewAbove

@Composable
fun QuizReviewScreen(
    onNavigateBack: () -> Unit,
    onParticipantClick: (instanceId: String, participant: QuizParticipant) -> Unit,
    viewModel: QuizReviewViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is QuizReviewEvent.NavigateBack -> onNavigateBack()
                is QuizReviewEvent.ShowError -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
                is QuizReviewEvent.ShowToast -> Toast.makeText(context, event.message.asString(context), Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (state.showCancelConfirm) {
        DefaultDialog(
            title = stringResource(R.string.cancel_quiz_confirm_title),
            message = stringResource(R.string.cancel_quiz_confirm_message),
            confirmText = stringResource(R.string.delete),
            cancelText = stringResource(R.string.cancel),
            onConfirm = viewModel::onConfirmCancelQuiz,
            onCancel = viewModel::onDismissCancelConfirm
        )
    }

    QuizReviewScreen(
        state = state,
        onBackClick = onNavigateBack,
        onPublishClick = viewModel::onPublishClick,
        onSearchChange = viewModel::onSearchQueryChange,
        onParticipantClick = { participant ->
            if (participant.sessionStatus == "finished") {
                onParticipantClick(viewModel.getInstanceId(), participant)
            } else {
                Toast.makeText(context, context.getString(R.string.participant_not_finished), Toast.LENGTH_SHORT).show()
            }
        },
        onCancelQuizClick = viewModel::onCancelQuizClick
    )
}

@Composable
fun QuizReviewScreen(
    state: QuizReviewState,
    onBackClick: () -> Unit,
    onPublishClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onParticipantClick: (QuizParticipant) -> Unit,
    onCancelQuizClick: () -> Unit
) {
    LayoutWithLargeBottomCard(
        scrollable = false,
        contentAbove = {
            ReviewAbove(
                title = state.instanceTitle,
                isPublishing = state.isPublishing,
                isPublished = state.isPublished,
                onBackClick = onBackClick,
                onPublishClick = onPublishClick
            )
        },
        content = {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        SearchBar(
                            text = state.searchQuery,
                            onQueryChange = onSearchChange,
                            placeholder = stringResource(R.string.search_participants),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        ParticipantSection(
                            title = stringResource(R.string.participants_passed),
                            participants = state.activeParticipants,
                            onParticipantClick = onParticipantClick,
                            reviewedCount = state.reviewedCount
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ParticipantSection(
                            title = stringResource(R.string.participants_not_started),
                            participants = state.notStartedParticipants,
                            onParticipantClick = null
                        )
                    }

                    DefaultButton(
                        text = stringResource(R.string.cancel_quiz),
                        onClick = onCancelQuizClick,
                        isButtonEnabled = !state.isCanceling,
                        isWidthLimited = false,
                        buttonColor = ExtraColors.negative,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun QuizReviewPreview() {
    AppPreview {
        QuizReviewScreen(
            state = QuizReviewState(
                instanceTitle = "Коллоквиум iOS",
                isLoading = false,
                participants = quizParticipantReviewExample,

            ),
            onBackClick = {},
            onPublishClick = {},
            onSearchChange = {},
            onParticipantClick = {},
            onCancelQuizClick = {}
        )
    }
}
