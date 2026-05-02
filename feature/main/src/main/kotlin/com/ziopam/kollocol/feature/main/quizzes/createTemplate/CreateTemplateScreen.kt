package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.animations.ShimmerTemplateSkeleton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.dialogs.DefaultDialog
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.common.AiPromptDialog

@Composable
fun CreateTemplateScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTemplateViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler(enabled = state.hasUnsavedChanges) {
        viewModel.onBackAttempt()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateTemplateEvent.NavigateBack -> onNavigateBack()
                is CreateTemplateEvent.ShowToast -> {
                    val text = when (val msg = event.message) {
                        is UiText.StringRes -> context.getString(msg.resId, *msg.args.toTypedArray())
                        is UiText.Dynamic -> msg.value
                    }
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (state.showAddQuestionSheet) {
        val editingQuestion = state.editingQuestionIndex?.let { state.questions[it] }
        AddQuestionSheet(
            onDismiss = viewModel::hideAddQuestionSheet,
            onSave = { question ->
                val editIndex = state.editingQuestionIndex
                if (editIndex != null) {
                    viewModel.editQuestion(editIndex, question)
                } else {
                    viewModel.addQuestion(question)
                }
            },
            initialQuestion = editingQuestion
        )
    }

    CreateTemplateScreen(
        title = state.title,
        quizType = state.quizType,
        randomOrder = state.randomOrder,
        questions = state.questions,
        isLoading = state.isLoading,
        isLoadingTemplate = state.isLoadingTemplate,
        isGeneratingQuestions = state.isGeneratingQuestions,
        isEditMode = viewModel.isEditMode,
        showDeleteConfirmDialog = state.showDeleteConfirmDialog,
        showUnsavedChangesDialog = state.showUnsavedChangesDialog,
        showGenerateQuestionsSheet = state.showGenerateQuestionsSheet,
        generateQuestionsPrompt = state.generateQuestionsPrompt,
        onNavigateBack = onNavigateBack,
        onBackAttempt = viewModel::onBackAttempt,
        onTitleChange = viewModel::onTitleChange,
        onQuizTypeToggle = viewModel::onQuizTypeToggle,
        onRandomOrderToggle = viewModel::onRandomOrderToggle,
        onAddQuestionClick = { viewModel.showAddQuestionSheet() },
        onEditQuestion = { index -> viewModel.showAddQuestionSheet(index) },
        onDeleteQuestion = viewModel::deleteQuestion,
        onSaveClick = viewModel::saveTemplate,
        onDeleteClick = viewModel::onShowDeleteConfirmDialog,
        onConfirmDelete = viewModel::deleteTemplate,
        onDismissDeleteDialog = viewModel::onDismissDeleteDialog,
        onConfirmBack = viewModel::onConfirmBack,
        onDismissUnsavedChangesDialog = viewModel::onDismissUnsavedChangesDialog,
        onGenerateQuestionsClick = viewModel::onShowGenerateQuestionsSheet,
        onGenerateQuestionsPromptChange = viewModel::onGenerateQuestionsPromptChange,
        onSubmitGenerateQuestions = { viewModel.generateMoreQuestions(state.generateQuestionsPrompt) },
        onDismissGenerateQuestionsSheet = viewModel::onDismissGenerateQuestionsSheet
    )
}

@Composable
fun CreateTemplateScreen(
    title: String,
    quizType: String,
    randomOrder: Boolean,
    questions: List<QuestionUiModel>,
    isLoading: Boolean,
    isLoadingTemplate: Boolean,
    isGeneratingQuestions: Boolean,
    isEditMode: Boolean,
    showDeleteConfirmDialog: Boolean,
    showUnsavedChangesDialog: Boolean,
    showGenerateQuestionsSheet: Boolean,
    generateQuestionsPrompt: String,
    onNavigateBack: () -> Unit,
    onBackAttempt: () -> Unit,
    onTitleChange: (String) -> Unit,
    onQuizTypeToggle: () -> Unit,
    onRandomOrderToggle: () -> Unit,
    onAddQuestionClick: () -> Unit,
    onEditQuestion: (Int) -> Unit,
    onDeleteQuestion: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmBack: () -> Unit,
    onDismissUnsavedChangesDialog: () -> Unit,
    onGenerateQuestionsClick: () -> Unit,
    onGenerateQuestionsPromptChange: (String) -> Unit,
    onSubmitGenerateQuestions: () -> Unit,
    onDismissGenerateQuestionsSheet: () -> Unit
) {
    if (showDeleteConfirmDialog) {
        DefaultDialog(
            title = stringResource(R.string.delete_template_confirm_title),
            message = stringResource(R.string.delete_template_confirm_message),
            confirmText = stringResource(R.string.delete),
            cancelText = stringResource(R.string.cancel),
            onConfirm = onConfirmDelete,
            onCancel = onDismissDeleteDialog
        )
    }

    if (showUnsavedChangesDialog) {
        DefaultDialog(
            title = stringResource(R.string.discard_changes_title),
            message = stringResource(R.string.discard_changes_message),
            confirmText = stringResource(R.string.discard_changes_confirm),
            cancelText = stringResource(R.string.cancel),
            onConfirm = onConfirmBack,
            onCancel = onDismissUnsavedChangesDialog
        )
    }

    if (showGenerateQuestionsSheet) {
        AiPromptDialog(
            title = stringResource(R.string.generate_more_questions),
            hint = stringResource(R.string.ai_prompt_hint),
            prompt = generateQuestionsPrompt,
            onPromptChange = onGenerateQuestionsPromptChange,
            onConfirm = onSubmitGenerateQuestions,
            onDismiss = onDismissGenerateQuestionsSheet
        )
    }

    LayoutWithLargeBottomCard(
        contentAbove = {
            CreateTemplateAbove(
                isLoading = isLoading,
                isEditMode = isEditMode,
                onBackAttempt = onBackAttempt,
                onSaveClick = onSaveClick,
                onDeleteClick = onDeleteClick
            )
        },
        content = {
            if (isLoadingTemplate) {
                ShimmerTemplateSkeleton()
            } else {
                CreateTemplateBelow(
                    title = title,
                    quizType = quizType,
                    randomOrder = randomOrder,
                    questions = questions,
                    isLoading = isLoading,
                    isGeneratingQuestions = isGeneratingQuestions,
                    onTitleChange = onTitleChange,
                    onQuizTypeToggle = onQuizTypeToggle,
                    onRandomOrderToggle = onRandomOrderToggle,
                    onAddQuestionClick = onAddQuestionClick,
                    onEditQuestion = onEditQuestion,
                    onDeleteQuestion = onDeleteQuestion,
                    onGenerateQuestionsClick = onGenerateQuestionsClick
                )
            }
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewLightDark
@Composable
private fun CreateTemplateEmptyPreview() {
    var title by remember { mutableStateOf("") }

    AppTheme {
        Scaffold {
            CreateTemplateScreen(
                title = title,
                quizType = "async",
                randomOrder = false,
                questions = emptyList(),
                isLoading = false,
                isLoadingTemplate = false,
                isGeneratingQuestions = false,
                isEditMode = false,
                showDeleteConfirmDialog = false,
                showUnsavedChangesDialog = false,
                showGenerateQuestionsSheet = false,
                generateQuestionsPrompt = "",
                onNavigateBack = {},
                onBackAttempt = {},
                onTitleChange = { title = it },
                onQuizTypeToggle = {},
                onRandomOrderToggle = {},
                onAddQuestionClick = {},
                onEditQuestion = {},
                onDeleteQuestion = {},
                onSaveClick = {},
                onDeleteClick = {},
                onConfirmDelete = {},
                onDismissDeleteDialog = {},
                onConfirmBack = {},
                onDismissUnsavedChangesDialog = {},
                onGenerateQuestionsClick = {},
                onGenerateQuestionsPromptChange = {},
                onSubmitGenerateQuestions = {},
                onDismissGenerateQuestionsSheet = {}
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewLightDark
@Composable
private fun CreateTemplateEditModePreview() {
    val sampleQuestions = listOf(
        QuestionUiModel(
            text = "Какой язык используется для Android?",
            type = QuestionType.SINGLE,
            options = listOf("Kotlin", "Swift", "Python", "Ruby"),
            correctOptionIndices = setOf(0),
            maxScore = 10,
            timeLimitSec = 30
        )
    )

    AppTheme {
        Scaffold {
            CreateTemplateScreen(
                title = "Мой тестовый квиз",
                quizType = "async",
                randomOrder = false,
                questions = sampleQuestions,
                isLoading = false,
                isLoadingTemplate = false,
                isGeneratingQuestions = false,
                isEditMode = true,
                showDeleteConfirmDialog = false,
                showUnsavedChangesDialog = false,
                showGenerateQuestionsSheet = false,
                generateQuestionsPrompt = "",
                onNavigateBack = {},
                onBackAttempt = {},
                onTitleChange = {},
                onQuizTypeToggle = {},
                onRandomOrderToggle = {},
                onAddQuestionClick = {},
                onEditQuestion = {},
                onDeleteQuestion = {},
                onSaveClick = {},
                onDeleteClick = {},
                onConfirmDelete = {},
                onDismissDeleteDialog = {},
                onConfirmBack = {},
                onDismissUnsavedChangesDialog = {},
                onGenerateQuestionsClick = {},
                onGenerateQuestionsPromptChange = {},
                onSubmitGenerateQuestions = {},
                onDismissGenerateQuestionsSheet = {}
            )
        }
    }
}
