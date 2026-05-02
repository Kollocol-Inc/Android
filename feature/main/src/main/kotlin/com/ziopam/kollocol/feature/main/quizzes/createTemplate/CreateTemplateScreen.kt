package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.feature.main.R

@Composable
fun CreateTemplateScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTemplateViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateTemplateEvent.NavigateBack -> onNavigateBack()
                is CreateTemplateEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            val message = when (error) {
                "fill_title" -> context.getString(R.string.fill_title_error)
                "add_question" -> context.getString(R.string.add_question_error)
                else -> error
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
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
        onNavigateBack = onNavigateBack,
        onTitleChange = viewModel::onTitleChange,
        onQuizTypeToggle = viewModel::onQuizTypeToggle,
        onRandomOrderToggle = viewModel::onRandomOrderToggle,
        onAddQuestionClick = { viewModel.showAddQuestionSheet() },
        onEditQuestion = { index -> viewModel.showAddQuestionSheet(index) },
        onDeleteQuestion = viewModel::deleteQuestion,
        onSaveClick = viewModel::saveTemplate
    )
}

@Composable
fun CreateTemplateScreen(
    title: String,
    quizType: String,
    randomOrder: Boolean,
    questions: List<QuestionUiModel>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onQuizTypeToggle: () -> Unit,
    onRandomOrderToggle: () -> Unit,
    onAddQuestionClick: () -> Unit,
    onEditQuestion: (Int) -> Unit,
    onDeleteQuestion: (Int) -> Unit,
    onSaveClick: () -> Unit
) {
    LayoutWithLargeBottomCard(
        contentAbove = { CreateTemplateAbove(isLoading = isLoading, onNavigateBack = onNavigateBack, onSaveClick = onSaveClick) },
        content = {
            CreateTemplateBelow(
                title = title,
                quizType = quizType,
                randomOrder = randomOrder,
                questions = questions,
                isLoading = isLoading,
                onTitleChange = onTitleChange,
                onQuizTypeToggle = onQuizTypeToggle,
                onRandomOrderToggle = onRandomOrderToggle,
                onAddQuestionClick = onAddQuestionClick,
                onEditQuestion = onEditQuestion,
                onDeleteQuestion = onDeleteQuestion
            )
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
                onNavigateBack = {},
                onTitleChange = { title = it },
                onQuizTypeToggle = {},
                onRandomOrderToggle = {},
                onAddQuestionClick = {},
                onEditQuestion = {},
                onDeleteQuestion = {},
                onSaveClick = {}
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewLightDark
@Composable
private fun CreateTemplateWithQuestionsPreview() {
    val sampleQuestions = listOf(
        QuestionUiModel(
            text = "Какой язык используется для Android?",
            type = QuestionType.SINGLE,
            options = listOf("Kotlin", "Swift", "Python", "Ruby"),
            correctOptionIndices = setOf(0),
            maxScore = 10,
            timeLimitSec = 30
        ),
        QuestionUiModel(
            text = "Выберите фреймворки для UI",
            type = QuestionType.MULTIPLE,
            options = listOf("Compose", "SwiftUI", "Flutter", ""),
            correctOptionIndices = setOf(0, 2),
            maxScore = 15,
            timeLimitSec = 45
        ),
        QuestionUiModel(
            text = "Что такое MVVM?",
            type = QuestionType.OPEN,
            correctAnswer = "Model-View-ViewModel",
            maxScore = 20,
            timeLimitSec = 60
        )
    )

    AppTheme {
        Scaffold {
            CreateTemplateScreen(
                title = "Мой тестовый квиз",
                quizType = "async",
                randomOrder = false,
                questions = sampleQuestions,
                isLoading = true,
                onNavigateBack = {},
                onTitleChange = {},
                onQuizTypeToggle = {},
                onRandomOrderToggle = {},
                onAddQuestionClick = {},
                onEditQuestion = {},
                onDeleteQuestion = {},
                onSaveClick = {}
            )
        }
    }
}
