package com.ziopam.kollocol.feature.main.quizzes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode
import com.ziopam.kollocol.domain.repository.QuizRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
class QuizzesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var quizRepository: QuizRepository
    private lateinit var viewModel: QuizzesViewModel

    private val runningFlow = MutableStateFlow<List<QuizInfo>>(emptyList())
    private val pendingFlow = MutableStateFlow<List<QuizInfo>>(emptyList())
    private val reviewedFlow = MutableStateFlow<List<QuizInfo>>(emptyList())
    private val templatesFlow = MutableStateFlow<List<QuizInfo>>(emptyList())

    private fun makeQuiz(id: String, title: String) = QuizInfo(
        id = id, title = title, accessCode = "CODE$id",
        totalQuestions = 5, mode = QuizMode.ASYNC
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        quizRepository = mockk(relaxed = true)

        every { quizRepository.runningQuizzes } returns runningFlow
        every { quizRepository.pendingQuizzes } returns pendingFlow
        every { quizRepository.reviewedQuizzes } returns reviewedFlow
        every { quizRepository.templates } returns templatesFlow

        viewModel = QuizzesViewModel(quizRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChange updates searchQuery in myQuizzesState`() = runTest {
        // Given - subscribe to activate stateIn
        backgroundScope.launch { viewModel.myQuizzesState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("kotlin")
        advanceUntilIdle()

        // Then
        assertEquals("kotlin", viewModel.myQuizzesState.value.searchQuery)
    }

    @Test
    fun `onTabSelected updates selectedTabIndex`() = runTest {
        // Given
        backgroundScope.launch { viewModel.myQuizzesState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onTabSelected(1)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.myQuizzesState.value.selectedTabIndex)
    }

    @Test
    fun `myQuizzesState filters runningQuizzes by search query`() = runTest {
        // Given
        runningFlow.value = listOf(
            makeQuiz("1", "Kotlin Quiz"),
            makeQuiz("2", "Java Quiz")
        )
        backgroundScope.launch { viewModel.myQuizzesState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("kotlin")
        advanceUntilIdle()

        // Then
        val state = viewModel.myQuizzesState.value
        assertEquals(1, state.runningQuizzes.size)
        assertEquals("Kotlin Quiz", state.runningQuizzes[0].title)
    }

    @Test
    fun `myQuizzesState filtering is case insensitive`() = runTest {
        // Given
        runningFlow.value = listOf(makeQuiz("1", "Kotlin Quiz"))
        backgroundScope.launch { viewModel.myQuizzesState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("KOTLIN")
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.myQuizzesState.value.runningQuizzes.size)
    }

    @Test
    fun `myQuizzesState returns all quizzes when query is blank`() = runTest {
        // Given
        runningFlow.value = listOf(makeQuiz("1", "Quiz A"), makeQuiz("2", "Quiz B"))
        backgroundScope.launch { viewModel.myQuizzesState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("  ")
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.myQuizzesState.value.runningQuizzes.size)
    }

    @Test
    fun `templatesState filters templates by search query`() = runTest {
        // Given
        templatesFlow.value = listOf(
            makeQuiz("1", "Android Template"),
            makeQuiz("2", "iOS Template")
        )
        backgroundScope.launch { viewModel.templatesState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("android")
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.templatesState.value.templates.size)
        assertEquals("Android Template", viewModel.templatesState.value.templates[0].title)
    }

    @Test
    fun `onStartClick sets startQuizState with template`() = runTest {
        // Given
        val template = makeQuiz("1", "My Template")

        // When
        viewModel.onStartClick(template)
        advanceUntilIdle()

        // Then
        val state = viewModel.startQuizState.value
        assertEquals(template, state.template)
        assertEquals(template.title, state.title)
    }

    @Test
    fun `onDismissStartQuiz clears startQuizState`() = runTest {
        // Given
        viewModel.onStartClick(makeQuiz("1", "Template"))
        advanceUntilIdle()

        // When
        viewModel.onDismissStartQuiz()
        advanceUntilIdle()

        // Then
        val state = viewModel.startQuizState.value
        assertNull(state.template)
        assertEquals("", state.title)
    }

    @Test
    fun `onStartQuizTitleChange updates title in startQuizState`() = runTest {
        // When
        viewModel.onStartQuizTitleChange("New Quiz Title")
        advanceUntilIdle()

        // Then
        assertEquals("New Quiz Title", viewModel.startQuizState.value.title)
    }

    @Test
    fun `onStartQuizDeadlineDateChange updates deadlineDateMillis`() = runTest {
        // Given
        val millis = 1700000000000L

        // When
        viewModel.onStartQuizDeadlineDateChange(millis)
        advanceUntilIdle()

        // Then
        assertEquals(millis, viewModel.startQuizState.value.deadlineDateMillis)
    }

    @Test
    fun `onStartQuizDeadlineTimeChange updates hour and minute`() = runTest {
        // When
        viewModel.onStartQuizDeadlineTimeChange(14, 30)
        advanceUntilIdle()

        // Then
        val state = viewModel.startQuizState.value
        assertEquals(14, state.deadlineHour)
        assertEquals(30, state.deadlineMinute)
    }

    @Test
    fun `startQuiz does nothing when template is null`() = runTest {
        // Given
        assertNull(viewModel.startQuizState.value.template)

        // When
        viewModel.startQuiz()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { quizRepository.createInstance(any(), any(), any()) }
    }

    @Test
    fun `startQuiz sets loading to true then false on success`() = runTest {
        // Given
        val template = makeQuiz("1", "Template")
        viewModel.onStartClick(template)
        coEvery { quizRepository.createInstance(any(), any(), any()) } returns AppResult.Ok(Unit)

        // When & Then
        viewModel.startQuizState.test {
            awaitItem()
            viewModel.startQuiz()
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            val doneState = awaitItem()
            assertFalse(doneState.isLoading)
        }
    }

    @Test
    fun `startQuiz emits QuizStarted event on success`() = runTest {
        // Given
        val template = makeQuiz("1", "Template")
        viewModel.onStartClick(template)
        coEvery { quizRepository.createInstance(any(), any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.events.test {
            viewModel.startQuiz()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is QuizzesEvent.QuizStarted)
        }
    }

    @Test
    fun `startQuiz clears startQuizState on success`() = runTest {
        // Given
        val template = makeQuiz("1", "Template")
        viewModel.onStartClick(template)
        coEvery { quizRepository.createInstance(any(), any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.startQuiz()
        advanceUntilIdle()

        // Then
        assertNull(viewModel.startQuizState.value.template)
    }

    @Test
    fun `startQuiz emits ShowError event on failure`() = runTest {
        // Given
        val template = makeQuiz("1", "Template")
        viewModel.onStartClick(template)
        coEvery { quizRepository.createInstance(any(), any(), any()) } returns
            AppResult.Err(AppError.NoInternet)

        // When
        viewModel.events.test {
            viewModel.startQuiz()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is QuizzesEvent.ShowError)
        }
    }

    @Test
    fun `startQuiz keeps loading false on failure`() = runTest {
        // Given
        val template = makeQuiz("1", "Template")
        viewModel.onStartClick(template)
        coEvery { quizRepository.createInstance(any(), any(), any()) } returns
            AppResult.Err(AppError.Timeout)

        // When
        viewModel.startQuiz()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.startQuizState.value.isLoading)
    }

    @Test
    fun `startQuiz calls createInstance with correct templateId and title`() = runTest {
        // Given
        val template = makeQuiz("template-id", "My Quiz")
        viewModel.onStartClick(template)
        viewModel.onStartQuizTitleChange("Custom Title")
        coEvery { quizRepository.createInstance(any(), any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.startQuiz()
        advanceUntilIdle()

        // Then
        coVerify { quizRepository.createInstance("template-id", "Custom Title", null) }
    }

    @Test
    fun `refresh calls getHostingQuizzes and getTemplates`() = runTest {
        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        coVerify { quizRepository.getHostingQuizzes(status = null) }
        coVerify { quizRepository.getHostingQuizzes(status = "pending_review") }
        coVerify { quizRepository.getHostingQuizzes(status = "reviewed") }
        coVerify { quizRepository.getTemplates() }
    }

    @Test
    fun `refreshTemplates calls only getTemplates`() = runTest {
        // When
        viewModel.refreshTemplates()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { quizRepository.getTemplates() }
    }
}
