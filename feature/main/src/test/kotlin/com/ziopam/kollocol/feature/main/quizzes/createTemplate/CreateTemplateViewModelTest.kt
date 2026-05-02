package com.ziopam.kollocol.feature.main.quizzes.createTemplate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.repository.QuizRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CreateTemplateViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var quizRepository: QuizRepository
    private lateinit var viewModel: CreateTemplateViewModel

    private fun makeQuestion(
        text: String = "Question?",
        type: QuestionType = QuestionType.SINGLE
    ) = QuestionUiModel(text = text, type = type)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        quizRepository = mockk(relaxed = true)
        every { quizRepository.runningQuizzes } returns MutableStateFlow(emptyList())
        every { quizRepository.pendingQuizzes } returns MutableStateFlow(emptyList())
        every { quizRepository.reviewedQuizzes } returns MutableStateFlow(emptyList())
        every { quizRepository.templates } returns MutableStateFlow(emptyList())
        viewModel = CreateTemplateViewModel(quizRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTitleChange updates title`() {
        // When
        viewModel.onTitleChange("New Template")

        // Then
        assertEquals("New Template", viewModel.uiState.value.title)
    }

    @Test
    fun `onQuizTypeToggle switches from async to sync`() {
        // Given
        assertEquals("async", viewModel.uiState.value.quizType)

        // When
        viewModel.onQuizTypeToggle()

        // Then
        assertEquals("sync", viewModel.uiState.value.quizType)
    }

    @Test
    fun `onQuizTypeToggle switches back from sync to async`() {
        // Given
        viewModel.onQuizTypeToggle()

        // When
        viewModel.onQuizTypeToggle()

        // Then
        assertEquals("async", viewModel.uiState.value.quizType)
    }

    @Test
    fun `onRandomOrderToggle enables randomOrder`() {
        // Given
        assertFalse(viewModel.uiState.value.randomOrder)

        // When
        viewModel.onRandomOrderToggle()

        // Then
        assertTrue(viewModel.uiState.value.randomOrder)
    }

    @Test
    fun `onRandomOrderToggle disables randomOrder after toggling twice`() {
        // When
        viewModel.onRandomOrderToggle()
        viewModel.onRandomOrderToggle()

        // Then
        assertFalse(viewModel.uiState.value.randomOrder)
    }

    @Test
    fun `showAddQuestionSheet sets showAddQuestionSheet to true`() {
        // When
        viewModel.showAddQuestionSheet()

        // Then
        assertTrue(viewModel.uiState.value.showAddQuestionSheet)
    }

    @Test
    fun `showAddQuestionSheet with editIndex sets editingQuestionIndex`() {
        // When
        viewModel.showAddQuestionSheet(editIndex = 2)

        // Then
        assertEquals(2, viewModel.uiState.value.editingQuestionIndex)
    }

    @Test
    fun `hideAddQuestionSheet hides sheet and clears editingQuestionIndex`() {
        // Given
        viewModel.showAddQuestionSheet(editIndex = 1)

        // When
        viewModel.hideAddQuestionSheet()

        // Then
        assertFalse(viewModel.uiState.value.showAddQuestionSheet)
        assertNull(viewModel.uiState.value.editingQuestionIndex)
    }

    @Test
    fun `addQuestion appends question to list`() {
        // Given
        val question = makeQuestion("What is Kotlin?")

        // When
        viewModel.addQuestion(question)

        // Then
        assertEquals(1, viewModel.uiState.value.questions.size)
        assertEquals("What is Kotlin?", viewModel.uiState.value.questions[0].text)
    }

    @Test
    fun `addQuestion hides the add question sheet`() {
        // Given
        viewModel.showAddQuestionSheet()

        // When
        viewModel.addQuestion(makeQuestion())

        // Then
        assertFalse(viewModel.uiState.value.showAddQuestionSheet)
    }

    @Test
    fun `addQuestion clears editingQuestionIndex`() {
        // Given
        viewModel.showAddQuestionSheet(editIndex = 0)

        // When
        viewModel.addQuestion(makeQuestion())

        // Then
        assertNull(viewModel.uiState.value.editingQuestionIndex)
    }

    @Test
    fun `editQuestion replaces question at given index`() {
        // Given
        viewModel.addQuestion(makeQuestion("Original"))
        val updated = makeQuestion("Updated")

        // When
        viewModel.editQuestion(0, updated)

        // Then
        assertEquals("Updated", viewModel.uiState.value.questions[0].text)
    }

    @Test
    fun `editQuestion hides the sheet`() {
        // Given
        viewModel.addQuestion(makeQuestion())
        viewModel.showAddQuestionSheet(editIndex = 0)

        // When
        viewModel.editQuestion(0, makeQuestion("Edited"))

        // Then
        assertFalse(viewModel.uiState.value.showAddQuestionSheet)
    }

    @Test
    fun `deleteQuestion removes question at given index`() {
        // Given
        viewModel.addQuestion(makeQuestion("Q1"))
        viewModel.addQuestion(makeQuestion("Q2"))
        viewModel.addQuestion(makeQuestion("Q3"))

        // When
        viewModel.deleteQuestion(1)

        // Then
        assertEquals(2, viewModel.uiState.value.questions.size)
        assertEquals("Q1", viewModel.uiState.value.questions[0].text)
        assertEquals("Q3", viewModel.uiState.value.questions[1].text)
    }

    @Test
    fun `saveTemplate sets error when title is blank`() {
        // Given
        viewModel.onTitleChange("")
        viewModel.addQuestion(makeQuestion())

        // When
        viewModel.saveTemplate()

        // Then
        assertEquals("fill_title", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `saveTemplate sets error when questions list is empty`() {
        // Given
        viewModel.onTitleChange("My Template")

        // When
        viewModel.saveTemplate()

        // Then
        assertEquals("add_question", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `saveTemplate does not call repository when title is blank`() = runTest {
        // Given
        viewModel.addQuestion(makeQuestion())

        // When
        viewModel.saveTemplate()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { quizRepository.createTemplate(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `saveTemplate calls repository with correct params on valid state`() = runTest {
        // Given
        viewModel.onTitleChange("Test Template")
        viewModel.onQuizTypeToggle()
        viewModel.onRandomOrderToggle()
        viewModel.addQuestion(makeQuestion("Q1", QuestionType.SINGLE))
        coEvery { quizRepository.createTemplate(any(), any(), any(), any(), any()) } returns
            AppResult.Ok("template-id")

        // When
        viewModel.saveTemplate()
        advanceUntilIdle()

        // Then
        coVerify {
            quizRepository.createTemplate(
                title = "Test Template",
                quizType = "sync",
                questions = any(),
                randomOrder = true
            )
        }
    }

    @Test
    fun `saveTemplate emits NavigateBack event on success`() = runTest {
        // Given
        viewModel.onTitleChange("Template")
        viewModel.addQuestion(makeQuestion())
        coEvery { quizRepository.createTemplate(any(), any(), any(), any(), any()) } returns
            AppResult.Ok("id")

        // When
        viewModel.events.test {
            viewModel.saveTemplate()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is CreateTemplateEvent.NavigateBack)
        }
    }

    @Test
    fun `saveTemplate sets loading to false on success`() = runTest {
        // Given
        viewModel.onTitleChange("Template")
        viewModel.addQuestion(makeQuestion())
        coEvery { quizRepository.createTemplate(any(), any(), any(), any(), any()) } returns
            AppResult.Ok("id")

        // When
        viewModel.saveTemplate()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `saveTemplate sets errorMessage on failure`() = runTest {
        // Given
        viewModel.onTitleChange("Template")
        viewModel.addQuestion(makeQuestion())
        coEvery { quizRepository.createTemplate(any(), any(), any(), any(), any()) } returns
            AppResult.Err(AppError.NoInternet)

        // When
        viewModel.saveTemplate()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.errorMessage != null)
    }

    @Test
    fun `saveTemplate sets loading to false on failure`() = runTest {
        // Given
        viewModel.onTitleChange("Template")
        viewModel.addQuestion(makeQuestion())
        coEvery { quizRepository.createTemplate(any(), any(), any(), any(), any()) } returns
            AppResult.Err(AppError.ServerError)

        // When
        viewModel.saveTemplate()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `clearError clears errorMessage`() {
        // Given
        viewModel.saveTemplate()

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `saveTemplate clears errorMessage before calling repository`() = runTest {
        // Given
        viewModel.onTitleChange("Template")
        viewModel.addQuestion(makeQuestion())
        viewModel.saveTemplate()

        coEvery { quizRepository.createTemplate(any(), any(), any(), any(), any()) } returns
            AppResult.Ok("id")

        // When
        viewModel.onTitleChange("Template 2")
        viewModel.saveTemplate()
        advanceUntilIdle()

        // Then
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
