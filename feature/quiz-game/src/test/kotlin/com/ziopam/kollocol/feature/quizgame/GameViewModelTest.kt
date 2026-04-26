package com.ziopam.kollocol.feature.quizgame

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.AnswerResult
import com.ziopam.kollocol.domain.model.ConnectionState
import com.ziopam.kollocol.domain.model.GameEvent
import com.ziopam.kollocol.domain.model.GameParticipant
import com.ziopam.kollocol.domain.model.GameQuestion
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import com.ziopam.kollocol.domain.model.QuestionType
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.GameRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
class GameViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var gameRepository: GameRepository
    private lateinit var personalRepository: PersonalRepository
    private lateinit var eventsFlow: MutableSharedFlow<GameEvent>
    private lateinit var viewModel: GameViewModel

    private fun makeQuestion(
        id: String = "q1",
        type: QuestionType = QuestionType.SINGLE,
        timeLimitMs: Long = 30_000L
    ) = GameQuestion(
        id = id, text = "Question?", type = type,
        questionIndex = 0, totalQuestions = 5,
        timeLimitMs = timeLimitMs, maxScore = 10
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        gameRepository = mockk(relaxed = true)
        personalRepository = mockk(relaxed = true)
        eventsFlow = MutableSharedFlow(extraBufferCapacity = 64)

        every { gameRepository.events } returns eventsFlow
        every { gameRepository.connectionState } returns MutableStateFlow(ConnectionState.IDLE)
        coEvery { personalRepository.getUser() } returns AppResult.Ok(
            User(avatarUrl = null, firstName = "Test", lastName = "User")
        )

        viewModel = GameViewModel(
            savedStateHandle = SavedStateHandle(mapOf("accessCode" to "TEST01")),
            gameRepository = gameRepository,
            personalRepository = personalRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial phase is Connecting`() {
        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Connecting)
    }

    @Test
    fun `initial accessCode is set from SavedStateHandle`() {
        // Then
        assertEquals("TEST01", viewModel.uiState.value.accessCode)
    }

    @Test
    fun `Connected with waiting status transitions to Lobby`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Connected("session1", "sync", "waiting", false))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Lobby)
    }

    @Test
    fun `Connected with waiting status and isCreator sets isCreator in Lobby`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Connected("session1", "sync", "waiting", true))
        advanceUntilIdle()

        // Then
        val lobby = viewModel.uiState.value.phase as GamePhase.Lobby
        assertTrue(lobby.isCreator)
    }

    @Test
    fun `Connected with finished status transitions to Finished`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Connected("session1", "sync", "finished", false))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Finished)
    }

    @Test
    fun `Connected with active status transitions to Playing`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Connected("session1", "sync", "active", false))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Playing)
    }

    @Test
    fun `QuizStarted transitions to Playing`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "waiting", false))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.QuizStarted)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Playing)
    }

    @Test
    fun `QuestionReceived updates phase with question`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        val question = makeQuestion()

        // When
        eventsFlow.emit(GameEvent.QuestionReceived(question))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertEquals(question, playing.question)
    }

    @Test
    fun `QuestionReceived resets hasAnswered to false`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertFalse(playing.hasAnswered)
    }

    @Test
    fun `AnswerResultReceived sets hasAnswered to true`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.AnswerResultReceived(AnswerResult(
            isCorrect = true, score = 10, timeSpentMs = 1000L, totalScore = 10
        )))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertTrue(playing.hasAnswered)
    }

    @Test
    fun `AnswerResultReceived emits ShowToast event`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            eventsFlow.emit(GameEvent.AnswerResultReceived(AnswerResult(
                isCorrect = true, score = 10, timeSpentMs = 500L, totalScore = 10
            )))
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is GameUiEvent.ShowToast)
        }
    }

    @Test
    fun `TimeExpired sets timeExpired and canContinue in Playing phase`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.TimeExpired(0))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertTrue(playing.timeExpired)
        assertTrue(playing.canContinue)
        assertEquals(0L, playing.timeRemainingMs)
    }

    @Test
    fun `TimeExpired emits ShowToast event`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            eventsFlow.emit(GameEvent.TimeExpired(0))
            advanceUntilIdle()

            // Then
            assertTrue(awaitItem() is GameUiEvent.ShowToast)
        }
    }

    @Test
    fun `Error with Session replaced while Connecting transitions to Error phase`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Error("Session replaced by another device"))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Error)
    }

    @Test
    fun `Error with Quiz already finished while Connecting transitions to Error phase`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Error("Quiz has already finished"))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Error)
    }

    @Test
    fun `Error while non-Connecting emits ShowToast`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "waiting", false))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            eventsFlow.emit(GameEvent.Error("Some error"))
            advanceUntilIdle()

            // Then
            assertTrue(awaitItem() is GameUiEvent.ShowToast)
        }
    }

    @Test
    fun `Error with You have been kicked while Connecting transitions to Error phase`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Error("You have been kicked"))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Error)
    }

    @Test
    fun `Error with You have been kicked while non-Connecting emits NavigateBack`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "waiting", false))
        advanceUntilIdle()

        // When
        viewModel.events.test {
            eventsFlow.emit(GameEvent.Error("You have been kicked"))
            advanceUntilIdle()

            // Then
            val first = awaitItem()
            assertTrue(first is GameUiEvent.ShowToast)
            val second = awaitItem()
            assertTrue(second is GameUiEvent.NavigateBack)
        }
    }

    @Test
    fun `QuizFinished transitions to Finished phase`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.QuizFinished(
            com.ziopam.kollocol.domain.model.GameFinishResult(finalScore = 50, rank = 2)
        ))
        advanceUntilIdle()

        // Then
        val finished = viewModel.uiState.value.phase as GamePhase.Finished
        assertEquals(50, finished.result?.finalScore)
        assertEquals(2, finished.result?.rank)
    }

    @Test
    fun `selectAnswer for SINGLE question sets exactly one answer`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.SINGLE)))
        advanceUntilIdle()

        // When
        viewModel.selectAnswer(2)
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertEquals(setOf(2), playing.selectedAnswers)
    }

    @Test
    fun `selectAnswer for SINGLE question replaces previous selection`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.SINGLE)))
        advanceUntilIdle()
        viewModel.selectAnswer(0)

        // When
        viewModel.selectAnswer(2)
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertEquals(setOf(2), playing.selectedAnswers)
    }

    @Test
    fun `selectAnswer for MULTIPLE question toggles selection`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.MULTIPLE)))
        advanceUntilIdle()

        // When
        viewModel.selectAnswer(0)
        viewModel.selectAnswer(1)
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertEquals(setOf(0, 1), playing.selectedAnswers)
    }

    @Test
    fun `selectAnswer for MULTIPLE removes answer on second selection`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.MULTIPLE)))
        advanceUntilIdle()
        viewModel.selectAnswer(0)

        // When
        viewModel.selectAnswer(0)
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertTrue(playing.selectedAnswers.isEmpty())
    }

    @Test
    fun `selectAnswer does nothing when hasAnswered is true`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.AnswerResultReceived(AnswerResult(
            isCorrect = true, score = 10, timeSpentMs = 500L, totalScore = 10
        )))
        advanceUntilIdle()

        // When
        viewModel.selectAnswer(0)
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertTrue(playing.selectedAnswers.isEmpty())
    }

    @Test
    fun `setOpenAnswer updates openAnswer in Playing phase`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.OPEN)))
        advanceUntilIdle()

        // When
        viewModel.setOpenAnswer("My answer")
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertEquals("My answer", playing.openAnswer)
    }

    @Test
    fun `nextQuestion calls sendContinue on gameRepository`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()

        // When
        viewModel.nextQuestion()
        advanceUntilIdle()

        // Then
        verify { gameRepository.sendContinue() }
    }

    @Test
    fun `startQuiz calls sendStartQuiz on gameRepository`() = runTest {
        // When
        viewModel.startQuiz()
        advanceUntilIdle()

        // Then
        verify { gameRepository.sendStartQuiz() }
    }

    @Test
    fun `retryConnect resets phase to Connecting`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Error("Quiz has already finished"))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.phase is GamePhase.Error)

        // When
        viewModel.retryConnect()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.phase is GamePhase.Connecting)
    }

    @Test
    fun `navigateBack emits NavigateBack event`() = runTest {
        // When
        viewModel.events.test {
            viewModel.navigateBack()
            advanceUntilIdle()

            // Then
            assertTrue(awaitItem() is GameUiEvent.NavigateBack)
        }
    }

    @Test
    fun `Disconnected while Playing sets isReconnecting to true`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.Disconnected())
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isReconnecting)
    }

    @Test
    fun `Disconnected while Connecting does not set isReconnecting`() = runTest {
        // When
        eventsFlow.emit(GameEvent.Disconnected())
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isReconnecting)
    }

    @Test
    fun `WaitingForCreator sets waitingForCreator in Playing phase`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.WaitingForCreator(questionIndex = 0, reason = null))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertTrue(playing.waitingForCreator)
        assertTrue(playing.canContinue)
        assertEquals(0L, playing.timeRemainingMs)
    }

    @Test
    fun `ParticipantsList updates quizName`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "waiting", false))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.ParticipantsList(
            participants = emptyList(),
            quizTitle = "My Quiz"
        ))
        advanceUntilIdle()

        // Then
        assertEquals("My Quiz", viewModel.uiState.value.quizName)
    }

    @Test
    fun `submitAnswer for SINGLE sends correct answer string`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.SINGLE)))
        advanceUntilIdle()
        viewModel.selectAnswer(2)
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        coVerify { gameRepository.sendAnswer("q1", "2", any()) }
    }

    @Test
    fun `submitAnswer for MULTIPLE sends bracket-formatted indices`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(id = "q2", type = QuestionType.MULTIPLE)))
        advanceUntilIdle()
        viewModel.selectAnswer(0)
        viewModel.selectAnswer(2)
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        coVerify { gameRepository.sendAnswer("q2", "[0, 2]", any()) }
    }

    @Test
    fun `submitAnswer for OPEN sends text answer`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(id = "q3", type = QuestionType.OPEN)))
        advanceUntilIdle()
        viewModel.setOpenAnswer("my answer")
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        coVerify { gameRepository.sendAnswer("q3", "my answer", any()) }
    }

    @Test
    fun `submitAnswer does nothing when hasAnswered is true`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.AnswerResultReceived(AnswerResult(
            isCorrect = true, score = 10, timeSpentMs = 500L, totalScore = 10
        )))
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { gameRepository.sendAnswer(any(), any(), any()) }
    }

    @Test
    fun `submitAnswer does nothing when timeExpired`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.TimeExpired(0))
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { gameRepository.sendAnswer(any(), any(), any()) }
    }

    @Test
    fun `submitAnswer for SINGLE does nothing when no answer selected`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.SINGLE)))
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { gameRepository.sendAnswer(any(), any(), any()) }
    }

    @Test
    fun `submitAnswer for OPEN does nothing when answer is blank`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.OPEN)))
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { gameRepository.sendAnswer(any(), any(), any()) }
    }

    @Test
    fun `submitAnswer sets hasAnswered to true`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion(type = QuestionType.SINGLE)))
        advanceUntilIdle()
        viewModel.selectAnswer(1)
        advanceUntilIdle()

        // When
        viewModel.submitAnswer()
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertTrue(playing.hasAnswered)
    }

    @Test
    fun `ParticipantsUpdate joined adds participant in Lobby`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "waiting", false))
        advanceUntilIdle()
        val participant = GameParticipant(userId = "u1", name = "Alice", email = "alice@x.com")

        // When
        eventsFlow.emit(GameEvent.ParticipantsUpdate(action = "joined", user = participant, count = 1))
        advanceUntilIdle()

        // Then
        val lobby = viewModel.uiState.value.phase as GamePhase.Lobby
        assertTrue(lobby.participants.any { it.userId == "u1" })
    }

    @Test
    fun `ParticipantsUpdate left removes participant from Lobby`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "waiting", false))
        advanceUntilIdle()
        val participant = GameParticipant(userId = "u1", name = "Alice", email = "alice@x.com")
        eventsFlow.emit(GameEvent.ParticipantsUpdate(action = "joined", user = participant, count = 1))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.ParticipantsUpdate(action = "left", user = participant, count = 0))
        advanceUntilIdle()

        // Then
        val lobby = viewModel.uiState.value.phase as GamePhase.Lobby
        assertTrue(lobby.participants.none { it.userId == "u1" })
    }

    @Test
    fun `ParticipantsUpdate in Playing updates leaderboard online status`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        val entry = LeaderboardEntry(rank = 1, userId = "u1", name = "Alice",
            email = "alice@x.com", avatarUrl = null, score = 0, isOnline = true)
        eventsFlow.emit(GameEvent.LeaderboardUpdate(leaderboard = listOf(entry)))
        advanceUntilIdle()
        val participant = GameParticipant(userId = "u1", name = "Alice", email = "alice@x.com")

        // When
        eventsFlow.emit(GameEvent.ParticipantsUpdate(action = "left", user = participant, count = 0))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        val updatedEntry = playing.leaderboard.first { it.userId == "u1" }
        assertFalse(updatedEntry.isOnline)
    }

    @Test
    fun `AnswerProgress updates answerProgress in Playing phase`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()
        eventsFlow.emit(GameEvent.QuestionReceived(makeQuestion()))
        advanceUntilIdle()

        // When
        eventsFlow.emit(GameEvent.AnswerProgress(participantsAnswered = 3, totalParticipants = 5))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertEquals(3, playing.answerProgress?.participantsAnswered)
        assertEquals(5, playing.answerProgress?.totalParticipants)
    }

    @Test
    fun `LeaderboardUpdate updates leaderboard in Playing phase`() = runTest {
        // Given
        eventsFlow.emit(GameEvent.Connected("s1", "sync", "active", false))
        advanceUntilIdle()

        val entry = LeaderboardEntry(rank = 1, userId = "u1", name = "Alice",
            email = "alice@x.com", avatarUrl = null, score = 42)

        // When
        eventsFlow.emit(GameEvent.LeaderboardUpdate(leaderboard = listOf(entry)))
        advanceUntilIdle()

        // Then
        val playing = viewModel.uiState.value.phase as GamePhase.Playing
        assertEquals(1, playing.leaderboard.size)
        assertEquals(42, playing.leaderboard[0].score)
    }

    @Test
    fun `kickParticipant calls sendKick on gameRepository`() = runTest {
        // When
        viewModel.kickParticipant("alice@x.com")
        advanceUntilIdle()

        // Then
        verify { gameRepository.sendKick("alice@x.com") }
    }
}
