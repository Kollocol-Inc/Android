package com.ziopam.kollocol.feature.main.quizzes.review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.ParticipantAnswers
import com.ziopam.kollocol.domain.model.ReviewAnswer
import com.ziopam.kollocol.domain.model.ReviewQuestion
import com.ziopam.kollocol.domain.repository.QuizRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ParticipantReviewViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var quizRepository: QuizRepository
    private lateinit var viewModel: ParticipantReviewViewModel

    private fun makeQuestion(
        id: String = "q1",
        text: String = "Question?",
        type: String = "single",
        maxScore: Int = 5,
        orderIndex: Int = 0
    ) = ReviewQuestion(
        id = id,
        text = text,
        type = type,
        options = listOf("A", "B"),
        correctAnswer = "A",
        maxScore = maxScore,
        orderIndex = orderIndex
    )

    private fun makeAnswer(
        questionId: String = "q1",
        answer: String = "A",
        isCorrect: Boolean = true,
        isReviewed: Boolean = false,
        score: Int = 0
    ) = ReviewAnswer(
        questionId = questionId,
        answer = answer,
        isCorrect = isCorrect,
        isReviewed = isReviewed,
        score = score
    )

    private fun makeSavedStateHandle() = SavedStateHandle(
        mapOf("instanceId" to "instance-1", "userId" to "user-1")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        quizRepository = mockk(relaxed = true)
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Test Quiz",
                questions = listOf(makeQuestion()),
                answers = listOf(makeAnswer())
            )
        )
        viewModel = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads participant answers`() = runTest {
        // When
        advanceUntilIdle()

        // Then
        assertEquals("Test Quiz", viewModel.state.value.instanceTitle)
        assertEquals(1, viewModel.state.value.questions.size)
        assertEquals(1, viewModel.state.value.answers.size)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `init sets currentQuestionIndex to 0`() = runTest {
        // When
        advanceUntilIdle()

        // Then
        assertEquals(0, viewModel.state.value.currentQuestionIndex)
    }

    @Test
    fun `init sets currentScore from first answer`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(makeQuestion("q1")),
                answers = listOf(makeAnswer("q1", score = 3))
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())

        // When
        advanceUntilIdle()

        // Then
        assertEquals(3, vm.state.value.currentScore)
    }

    @Test
    fun `init sorts questions by orderIndex`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(
                    makeQuestion("q2", orderIndex = 1),
                    makeQuestion("q1", orderIndex = 0)
                ),
                answers = emptyList()
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())

        // When
        advanceUntilIdle()

        // Then
        assertEquals("q1", vm.state.value.questions[0].id)
        assertEquals("q2", vm.state.value.questions[1].id)
    }

    @Test
    fun `init emits ShowError when getParticipantAnswers fails`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Err(AppError.NoInternet)
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())

        // When & Then
        vm.events.test {
            advanceUntilIdle()
            assertTrue(awaitItem() is ParticipantReviewEvent.ShowError)
        }
    }

    @Test
    fun `init sets isLoading to false on failure`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Err(AppError.ServerError)
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())

        // When
        advanceUntilIdle()

        // Then
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `currentQuestion returns question at currentQuestionIndex`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(makeQuestion("q1"), makeQuestion("q2")),
                answers = emptyList()
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
        advanceUntilIdle()

        // When
        vm.onSelectQuestion(1)

        // Then
        assertEquals("q2", vm.state.value.currentQuestion?.id)
    }

    @Test
    fun `currentAnswer returns answer matching currentQuestion`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(makeQuestion("q1"), makeQuestion("q2")),
                answers = listOf(makeAnswer("q1", answer = "A"), makeAnswer("q2", answer = "B"))
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
        advanceUntilIdle()

        // When
        vm.onSelectQuestion(1)

        // Then
        assertEquals("B", vm.state.value.currentAnswer?.answer)
    }

    @Test
    fun `onSelectQuestion updates currentQuestionIndex`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(makeQuestion("q1"), makeQuestion("q2")),
                answers = emptyList()
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
        advanceUntilIdle()

        // When
        vm.onSelectQuestion(1)

        // Then
        assertEquals(1, vm.state.value.currentQuestionIndex)
    }

    @Test
    fun `onSelectQuestion sets currentScore from answer score`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(makeQuestion("q1"), makeQuestion("q2")),
                answers = listOf(makeAnswer("q1", score = 2), makeAnswer("q2", score = 4))
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
        advanceUntilIdle()

        // When
        vm.onSelectQuestion(1)

        // Then
        assertEquals(4, vm.state.value.currentScore)
    }

    @Test
    fun `onSelectQuestion sets currentScore to 0 when no answer exists`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(makeQuestion("q1"), makeQuestion("q2")),
                answers = listOf(makeAnswer("q1", score = 3))
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
        advanceUntilIdle()

        // When
        vm.onSelectQuestion(1)

        // Then
        assertEquals(0, vm.state.value.currentScore)
    }

    @Test
    fun `onScoreIncrement increases currentScore by 1`() = runTest {
        // Given
        advanceUntilIdle()

        // When
        viewModel.onScoreIncrement()

        // Then
        assertEquals(1, viewModel.state.value.currentScore)
    }

    @Test
    fun `onScoreIncrement does not exceed maxScore`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(
                instanceTitle = "Quiz",
                questions = listOf(makeQuestion("q1", maxScore = 2)),
                answers = listOf(makeAnswer("q1", score = 2))
            )
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
        advanceUntilIdle()

        // When
        vm.onScoreIncrement()

        // Then
        assertEquals(2, vm.state.value.currentScore)
    }

    @Test
    fun `onScoreDecrement decreases currentScore by 1`() = runTest {
        // Given
        advanceUntilIdle()
        viewModel.onScoreIncrement()

        // When
        viewModel.onScoreDecrement()

        // Then
        assertEquals(0, viewModel.state.value.currentScore)
    }

    @Test
    fun `onScoreDecrement does not go below 0`() = runTest {
        // Given
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.currentScore)

        // When
        viewModel.onScoreDecrement()

        // Then
        assertEquals(0, viewModel.state.value.currentScore)
    }

    @Test
    fun `onGradeClick calls gradeAnswer with correct params`() = runTest {
        // Given
        advanceUntilIdle()
        viewModel.onScoreIncrement()
        coEvery { quizRepository.gradeAnswer(any(), any(), any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.onGradeClick()
        advanceUntilIdle()

        // Then
        coVerify { quizRepository.gradeAnswer("instance-1", "user-1", "q1", 1) }
    }

    @Test
    fun `onGradeClick emits ShowToast on success`() = runTest {
        // Given
        advanceUntilIdle()
        coEvery { quizRepository.gradeAnswer(any(), any(), any(), any()) } returns AppResult.Ok(Unit)

        // When & Then
        viewModel.events.test {
            viewModel.onGradeClick()
            advanceUntilIdle()

            assertTrue(awaitItem() is ParticipantReviewEvent.ShowToast)
        }
    }

    @Test
    fun `onGradeClick marks answer as reviewed on success`() = runTest {
        // Given
        advanceUntilIdle()
        coEvery { quizRepository.gradeAnswer(any(), any(), any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.onGradeClick()
        advanceUntilIdle()

        // Then
        val answer = viewModel.state.value.answers.find { it.questionId == "q1" }
        assertTrue(answer?.isReviewed == true)
    }

    @Test
    fun `onGradeClick updates answer score on success`() = runTest {
        // Given
        advanceUntilIdle()
        viewModel.onScoreIncrement()
        viewModel.onScoreIncrement()
        coEvery { quizRepository.gradeAnswer(any(), any(), any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.onGradeClick()
        advanceUntilIdle()

        // Then
        val answer = viewModel.state.value.answers.find { it.questionId == "q1" }
        assertEquals(2, answer?.score)
    }

    @Test
    fun `onGradeClick emits ShowError on failure`() = runTest {
        // Given
        advanceUntilIdle()
        coEvery { quizRepository.gradeAnswer(any(), any(), any(), any()) } returns AppResult.Err(AppError.ServerError)

        // When & Then
        viewModel.events.test {
            viewModel.onGradeClick()
            advanceUntilIdle()

            assertTrue(awaitItem() is ParticipantReviewEvent.ShowError)
        }
    }

    @Test
    fun `onGradeClick sets isGrading to false on failure`() = runTest {
        // Given
        advanceUntilIdle()
        coEvery { quizRepository.gradeAnswer(any(), any(), any(), any()) } returns AppResult.Err(AppError.NoInternet)

        // When
        viewModel.onGradeClick()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isGrading)
    }

    @Test
    fun `onGradeClick does nothing when currentQuestion is null`() = runTest {
        // Given
        coEvery { quizRepository.getParticipantAnswers(any(), any()) } returns AppResult.Ok(
            ParticipantAnswers(instanceTitle = "Quiz", questions = emptyList(), answers = emptyList())
        )
        val vm = ParticipantReviewViewModel(quizRepository, makeSavedStateHandle())
        advanceUntilIdle()

        // When
        vm.onGradeClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { quizRepository.gradeAnswer(any(), any(), any(), any()) }
    }
}
