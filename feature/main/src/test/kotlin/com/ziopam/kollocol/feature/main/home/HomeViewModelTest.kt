package com.ziopam.kollocol.feature.main.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.NotificationRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var quizRepository: QuizRepository
    private lateinit var personalRepository: PersonalRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var viewModel: HomeViewModel

    private val participatingFlow = MutableStateFlow<List<QuizInfo>>(emptyList())
    private val runningFlow = MutableStateFlow<List<QuizInfo>>(emptyList())
    private val userFlow = MutableStateFlow(User(avatarUrl = null, firstName = "John", lastName = "Doe"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        quizRepository = mockk(relaxed = true)
        personalRepository = mockk(relaxed = true)
        notificationRepository = mockk(relaxed = true)

        every { quizRepository.participatingQuizzes } returns participatingFlow
        every { quizRepository.runningQuizzes } returns runningFlow
        every { personalRepository.getUserFlow() } returns userFlow
        coEvery { personalRepository.getUser() } returns AppResult.Ok(
            User(avatarUrl = null, firstName = "John", lastName = "Doe")
        )

        viewModel = HomeViewModel(quizRepository, personalRepository, notificationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onCodeChanged updates quizCode in uiState`() = runTest {
        // Given
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onCodeChanged("ABC123")
        advanceUntilIdle()

        // Then
        assertEquals("ABC123", viewModel.uiState.value.quizCode)
    }

    @Test
    fun `onCodeChanged truncates code to 6 characters`() = runTest {
        // Given
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onCodeChanged("ABC1234567")
        advanceUntilIdle()

        // Then
        assertEquals("ABC123", viewModel.uiState.value.quizCode)
    }

    @Test
    fun `onCodeChanged allows empty string`() = runTest {
        // Given
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        viewModel.onCodeChanged("ABC123")
        advanceUntilIdle()

        // When
        viewModel.onCodeChanged("")
        advanceUntilIdle()

        // Then
        assertEquals("", viewModel.uiState.value.quizCode)
    }

    @Test
    fun `onCodeChanged allows exactly 6 characters`() = runTest {
        // Given
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onCodeChanged("123456")
        advanceUntilIdle()

        // Then
        assertEquals("123456", viewModel.uiState.value.quizCode)
    }

    @Test
    fun `uiState personName combines firstName and lastName`() = runTest {
        // Given
        userFlow.value = User(avatarUrl = null, firstName = "Jane", lastName = "Smith")
        viewModel = HomeViewModel(quizRepository, personalRepository, notificationRepository)
        backgroundScope.launch { viewModel.uiState.collect {} }

        // When
        advanceUntilIdle()

        // Then
        assertEquals("Jane Smith", viewModel.uiState.value.personName)
    }

    @Test
    fun `uiState personName trims when lastName is empty`() = runTest {
        // Given
        userFlow.value = User(avatarUrl = null, firstName = "Jane", lastName = "")
        viewModel = HomeViewModel(quizRepository, personalRepository, notificationRepository)
        backgroundScope.launch { viewModel.uiState.collect {} }

        // When
        advanceUntilIdle()

        // Then
        assertEquals("Jane", viewModel.uiState.value.personName)
    }

    @Test
    fun `refresh calls personalRepository getUser`() = runTest {
        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        coVerify(atLeast = 1) { personalRepository.getUser() }
    }

    @Test
    fun `refresh calls quizRepository getParticipatingQuizzes`() = runTest {
        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        coVerify(atLeast = 1) { quizRepository.getParticipatingQuizzes() }
    }

    @Test
    fun `refresh calls quizRepository getHostingQuizzes`() = runTest {
        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        coVerify(atLeast = 1) { quizRepository.getHostingQuizzes() }
    }

    @Test
    fun `uiState personName updates when getUserFlow emits new user`() = runTest {
        // Given
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        userFlow.value = User(avatarUrl = null, firstName = "Alice", lastName = "Wonder")
        advanceUntilIdle()

        // Then
        assertEquals("Alice Wonder", viewModel.uiState.value.personName)
    }

    @Test
    fun `refresh does not update personName when getUser fails`() = runTest {
        // Given
        userFlow.value = User(avatarUrl = null, firstName = "Initial", lastName = "Name")
        viewModel = HomeViewModel(quizRepository, personalRepository, notificationRepository)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()
        coEvery { personalRepository.getUser() } returns AppResult.Err(AppError.NoInternet)

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        assertEquals("Initial Name", viewModel.uiState.value.personName)
    }

    @Test
    fun `uiState reflects participatingQuizzes from repository`() = runTest {
        // Given
        val quiz = QuizInfo(
            id = "1", title = "Quiz 1", accessCode = "CODE1",
            totalQuestions = 5, mode = QuizMode.ASYNC
        )
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        participatingFlow.value = listOf(quiz)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.uiState.value.participatingQuizzes.size)
        assertEquals("Quiz 1", viewModel.uiState.value.participatingQuizzes[0].title)
    }

    @Test
    fun `uiState reflects runningQuizzes from repository`() = runTest {
        // Given
        val quiz = QuizInfo(
            id = "2", title = "Running Quiz", accessCode = "CODE2",
            totalQuestions = 3, mode = QuizMode.SYNC
        )
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        // When
        runningFlow.value = listOf(quiz)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.uiState.value.hostingQuizzes.size)
        assertEquals("Running Quiz", viewModel.uiState.value.hostingQuizzes[0].title)
    }
}
