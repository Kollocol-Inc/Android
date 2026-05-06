package com.ziopam.kollocol.feature.main.quizzes.review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.QuizInstanceDetails
import com.ziopam.kollocol.domain.model.QuizParticipant
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
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class QuizReviewViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var quizRepository: QuizRepository
    private lateinit var viewModel: QuizReviewViewModel

    private fun makeParticipant(
        userId: String = "u1",
        sessionStatus: String = "finished",
        reviewStatus: String = "not_reviewed",
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "john@test.com"
    ) = QuizParticipant(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        avatarUrl = null,
        sessionStatus = sessionStatus,
        reviewStatus = reviewStatus,
        totalScore = 0,
        maxPossibleScore = 10
    )

    private fun makeInstanceDetails(
        title: String = "Test Quiz",
        status: String = "in_progress",
        deadline: String? = null
    ) = QuizInstanceDetails(title = title, status = status, deadline = deadline)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        quizRepository = mockk(relaxed = true)
        coEvery { quizRepository.getInstanceById(any()) } returns AppResult.Ok(makeInstanceDetails())
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(emptyList())
        viewModel = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads instance details and participants`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceById("instance-1") } returns AppResult.Ok(makeInstanceDetails("My Quiz"))
        coEvery { quizRepository.getInstanceParticipants("instance-1") } returns AppResult.Ok(
            listOf(makeParticipant())
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))

        // When
        advanceUntilIdle()

        // Then
        assertEquals("My Quiz", vm.state.value.instanceTitle)
        assertEquals(1, vm.state.value.participants.size)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `init emits ShowError when getInstanceById fails`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceById(any()) } returns AppResult.Err(AppError.NoInternet)
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))

        // When & Then
        vm.events.test {
            advanceUntilIdle()
            assertTrue(awaitItem() is QuizReviewEvent.ShowError)
        }
    }

    @Test
    fun `init emits ShowError when getInstanceParticipants fails`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Err(AppError.ServerError)
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))

        // When & Then
        vm.events.test {
            advanceUntilIdle()
            assertTrue(awaitItem() is QuizReviewEvent.ShowError)
        }
    }

    @Test
    fun `onSearchQueryChange updates searchQuery`() = runTest {
        // Given
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("alice")
        advanceUntilIdle()

        // Then
        assertEquals("alice", viewModel.state.value.searchQuery)
    }

    @Test
    fun `activeParticipants returns only in_progress and finished participants`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", sessionStatus = "in_progress"),
                makeParticipant("u2", sessionStatus = "finished"),
                makeParticipant("u3", sessionStatus = "not_started"),
                makeParticipant("u4", sessionStatus = "joined")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When
        val active = vm.state.value.activeParticipants

        // Then
        assertEquals(2, active.size)
        assertTrue(active.all { it.sessionStatus == "in_progress" || it.sessionStatus == "finished" })
    }

    @Test
    fun `notStartedParticipants returns only not_started and joined participants`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", sessionStatus = "finished"),
                makeParticipant("u2", sessionStatus = "not_started"),
                makeParticipant("u3", sessionStatus = "joined")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When
        val notStarted = vm.state.value.notStartedParticipants

        // Then
        assertEquals(2, notStarted.size)
        assertTrue(notStarted.all { it.sessionStatus == "not_started" || it.sessionStatus == "joined" })
    }

    @Test
    fun `activeParticipants filters by searchQuery on name`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", sessionStatus = "finished", firstName = "Alice", lastName = "Smith"),
                makeParticipant("u2", sessionStatus = "finished", firstName = "Bob", lastName = "Jones")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When
        vm.onSearchQueryChange("alice")
        advanceUntilIdle()

        // Then
        assertEquals(1, vm.state.value.activeParticipants.size)
        assertEquals("Alice", vm.state.value.activeParticipants[0].firstName)
    }

    @Test
    fun `activeParticipants filters by searchQuery on email`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", sessionStatus = "finished", email = "alice@test.com"),
                makeParticipant("u2", sessionStatus = "finished", email = "bob@test.com")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When
        vm.onSearchQueryChange("bob@")
        advanceUntilIdle()

        // Then
        assertEquals(1, vm.state.value.activeParticipants.size)
    }

    @Test
    fun `reviewedCount counts only reviewed finished participants`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", sessionStatus = "finished", reviewStatus = "reviewed"),
                makeParticipant("u2", sessionStatus = "finished", reviewStatus = "not_reviewed"),
                makeParticipant("u3", sessionStatus = "in_progress", reviewStatus = "reviewed")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // Then
        assertEquals(1, vm.state.value.reviewedCount)
    }

    @Test
    fun `allReviewed is true when all finished participants are reviewed`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", sessionStatus = "finished", reviewStatus = "reviewed"),
                makeParticipant("u2", sessionStatus = "finished", reviewStatus = "reviewed")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // Then
        assertTrue(vm.state.value.allReviewed)
    }

    @Test
    fun `allReviewed is false when some finished participants are not reviewed`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", sessionStatus = "finished", reviewStatus = "reviewed"),
                makeParticipant("u2", sessionStatus = "finished", reviewStatus = "not_reviewed")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // Then
        assertFalse(vm.state.value.allReviewed)
    }

    @Test
    fun `allReviewed is false when there are no finished participants`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(makeParticipant("u1", sessionStatus = "not_started"))
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // Then
        assertFalse(vm.state.value.allReviewed)
    }

    @Test
    fun `isPublished is true when instanceStatus is published_results`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceById(any()) } returns AppResult.Ok(
            makeInstanceDetails(status = "published_results")
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // Then
        assertTrue(vm.state.value.isPublished)
    }

    @Test
    fun `isPublished is false when instanceStatus is not published_results`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceById(any()) } returns AppResult.Ok(
            makeInstanceDetails(status = "in_progress")
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // Then
        assertFalse(vm.state.value.isPublished)
    }

    @Test
    fun `onCancelQuizClick sets showCancelConfirm to true`() = runTest {
        // When
        viewModel.onCancelQuizClick()

        // Then
        assertTrue(viewModel.state.value.showCancelConfirm)
    }

    @Test
    fun `onDismissCancelConfirm clears showCancelConfirm`() = runTest {
        // Given
        viewModel.onCancelQuizClick()

        // When
        viewModel.onDismissCancelConfirm()

        // Then
        assertFalse(viewModel.state.value.showCancelConfirm)
    }

    @Test
    fun `onConfirmCancelQuiz calls deleteInstance and emits NavigateBack on success`() = runTest {
        // Given
        coEvery { quizRepository.deleteInstance("instance-1") } returns AppResult.Ok(Unit)

        // When & Then
        viewModel.events.test {
            viewModel.onConfirmCancelQuiz()
            advanceUntilIdle()

            assertTrue(awaitItem() is QuizReviewEvent.NavigateBack)
        }
        coVerify { quizRepository.deleteInstance("instance-1") }
    }

    @Test
    fun `onConfirmCancelQuiz emits ShowError on failure`() = runTest {
        // Given
        coEvery { quizRepository.deleteInstance(any()) } returns AppResult.Err(AppError.ServerError)

        // When & Then
        viewModel.events.test {
            viewModel.onConfirmCancelQuiz()
            advanceUntilIdle()

            assertTrue(awaitItem() is QuizReviewEvent.ShowError)
        }
    }

    @Test
    fun `onConfirmCancelQuiz sets isCanceling to false on failure`() = runTest {
        // Given
        coEvery { quizRepository.deleteInstance(any()) } returns AppResult.Err(AppError.NoInternet)

        // When
        viewModel.onConfirmCancelQuiz()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isCanceling)
    }

    @Test
    fun `onPublishClick emits ShowToast when already published`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceById(any()) } returns AppResult.Ok(
            makeInstanceDetails(status = "published_results")
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When & Then
        vm.events.test {
            vm.onPublishClick()
            advanceUntilIdle()

            assertTrue(awaitItem() is QuizReviewEvent.ShowToast)
        }
        coVerify(exactly = 0) { quizRepository.publishResults(any()) }
    }

    @Test
    fun `onPublishClick emits ShowToast when deadline has not passed`() = runTest {
        // Given
        val futureDeadline = Instant.now().plusSeconds(3600).toString()
        coEvery { quizRepository.getInstanceById(any()) } returns AppResult.Ok(
            makeInstanceDetails(deadline = futureDeadline)
        )
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(makeParticipant(reviewStatus = "reviewed"))
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When & Then
        vm.events.test {
            vm.onPublishClick()
            advanceUntilIdle()

            assertTrue(awaitItem() is QuizReviewEvent.ShowToast)
        }
        coVerify(exactly = 0) { quizRepository.publishResults(any()) }
    }

    @Test
    fun `onPublishClick emits ShowToast when not all participants reviewed`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(
                makeParticipant("u1", reviewStatus = "reviewed"),
                makeParticipant("u2", reviewStatus = "not_reviewed")
            )
        )
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When & Then
        vm.events.test {
            vm.onPublishClick()
            advanceUntilIdle()

            assertTrue(awaitItem() is QuizReviewEvent.ShowToast)
        }
        coVerify(exactly = 0) { quizRepository.publishResults(any()) }
    }

    @Test
    fun `onPublishClick calls publishResults and emits NavigateBack on success`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(makeParticipant(reviewStatus = "reviewed"))
        )
        coEvery { quizRepository.publishResults("instance-1") } returns AppResult.Ok(Unit)
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When & Then
        vm.events.test {
            vm.onPublishClick()
            advanceUntilIdle()

            assertTrue(awaitItem() is QuizReviewEvent.NavigateBack)
        }
        coVerify { quizRepository.publishResults("instance-1") }
    }

    @Test
    fun `onPublishClick emits ShowError on publishResults failure`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(makeParticipant(reviewStatus = "reviewed"))
        )
        coEvery { quizRepository.publishResults(any()) } returns AppResult.Err(AppError.ServerError)
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When & Then
        vm.events.test {
            vm.onPublishClick()
            advanceUntilIdle()

            assertTrue(awaitItem() is QuizReviewEvent.ShowError)
        }
    }

    @Test
    fun `onPublishClick sets isPublishing to false on failure`() = runTest {
        // Given
        coEvery { quizRepository.getInstanceParticipants(any()) } returns AppResult.Ok(
            listOf(makeParticipant(reviewStatus = "reviewed"))
        )
        coEvery { quizRepository.publishResults(any()) } returns AppResult.Err(AppError.NoInternet)
        val vm = QuizReviewViewModel(quizRepository, SavedStateHandle(mapOf("instanceId" to "instance-1")))
        advanceUntilIdle()

        // When
        vm.onPublishClick()
        advanceUntilIdle()

        // Then
        assertFalse(vm.state.value.isPublishing)
    }

    @Test
    fun `getInstanceId returns correct instanceId`() {
        // Then
        assertEquals("instance-1", viewModel.getInstanceId())
    }

    @Test
    fun `loadInstanceAndParticipants sets isLoading to false after completion`() = runTest {
        // When
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
    }
}
