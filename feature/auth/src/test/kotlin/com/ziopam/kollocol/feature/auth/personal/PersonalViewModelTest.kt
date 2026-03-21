package com.ziopam.kollocol.feature.auth.personal

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.uiText.UiText
import com.ziopam.kollocol.domain.repository.UserRepository
import com.ziopam.kollocol.feature.auth.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import com.ziopam.kollocol.core.ui.R as coreR

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: PersonalViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk(relaxed = true)
        viewModel = PersonalViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onFirstNameChanged updates firstName and clears Fields error`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John")
        viewModel.onButtonClick()
        advanceUntilIdle()

        // When
        viewModel.onFirstNameChanged("Jane")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Jane", state.firstName)
            assertNull(state.error)
        }
    }

    @Test
    fun `onFirstNameChanged sanitizes newlines`() = runTest {
        // When
        viewModel.onFirstNameChanged("John\nDoe")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John Doe", state.firstName)
        }
    }

    @Test
    fun `onFirstNameChanged sanitizes carriage returns`() = runTest {
        // When
        viewModel.onFirstNameChanged("John\rDoe")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John Doe", state.firstName)
        }
    }

    @Test
    fun `onFirstNameChanged limits length to 50 characters`() = runTest {
        // Given
        val longName = "a".repeat(100)

        // When
        viewModel.onFirstNameChanged(longName)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(50, state.firstName.length)
        }
    }

    @Test
    fun `onLastNameChanged updates lastName and clears Fields error`() = runTest {
        // Given
        viewModel.onLastNameChanged("Doe")
        viewModel.onButtonClick()
        advanceUntilIdle()

        // When
        viewModel.onLastNameChanged("Smith")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Smith", state.lastName)
            assertNull(state.error)
        }
    }

    @Test
    fun `onLastNameChanged sanitizes input`() = runTest {
        // When
        viewModel.onLastNameChanged("Doe\n\rTest")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Doe  Test", state.lastName)
        }
    }

    @Test
    fun `onAvatarSelected updates avatarUri and clears Avatar error`() = runTest {
        // Given
        val uri = mockk<Uri>()
        viewModel.onAvatarError(UiText.StringRes(coreR.string.file_too_big))

        // When
        viewModel.onAvatarSelected(uri)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(uri, state.avatarUri)
            assertNull(state.error)
        }
    }

    @Test
    fun `onAvatarError sets Avatar error`() = runTest {
        // Given
        val errorMessage = UiText.StringRes(coreR.string.file_too_big)

        // When
        viewModel.onAvatarError(errorMessage)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.error is PersonalError.Avatar)
            assertEquals(errorMessage, (state.error as PersonalError.Avatar).message)
        }
    }

    @Test
    fun `onAvatarRemove clears avatar and Avatar error`() = runTest {
        // Given
        val uri = mockk<Uri>()
        viewModel.onAvatarSelected(uri)
        viewModel.onAvatarError(UiText.StringRes(coreR.string.file_too_big))

        // When
        viewModel.onAvatarRemove()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.avatarUri)
            assertNull(state.error)
        }
    }

    @Test
    fun `onButtonClick trims and normalizes whitespace`() = runTest {
        // Given
        viewModel.onFirstNameChanged("  John   Doe  ")
        viewModel.onLastNameChanged("  Smith   Test  ")

        // When
        viewModel.onButtonClick()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John Doe", state.firstName)
            assertEquals("Smith Test", state.lastName)
        }
    }

    @Test
    fun `onButtonClick shows error when firstName is too short`() = runTest {
        // Given
        viewModel.onFirstNameChanged("J")
        viewModel.onLastNameChanged("Smith")

        // When
        viewModel.onButtonClick()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.error is PersonalError.Fields)
            val error = (state.error as PersonalError.Fields).message
            assertTrue(error is UiText.StringRes)
            assertEquals(R.string.name_length_error, (error as UiText.StringRes).resId)
        }
    }

    @Test
    fun `onButtonClick shows error when lastName is too short`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John")
        viewModel.onLastNameChanged("S")

        // When
        viewModel.onButtonClick()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.error is PersonalError.Fields)
        }
    }

    @Test
    fun `onButtonClick calls registerUser when names are valid`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John")
        viewModel.onLastNameChanged("Smith")
        coEvery { userRepository.registerUser(any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.onButtonClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { userRepository.registerUser("John", "Smith") }
    }

    @Test
    fun `onButtonClick accepts minimum valid length names`() = runTest {
        // Given
        viewModel.onFirstNameChanged("Jo")
        viewModel.onLastNameChanged("Sm")
        coEvery { userRepository.registerUser(any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.onButtonClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { userRepository.registerUser("Jo", "Sm") }
    }

    @Test
    fun `onButtonClick accepts maximum valid length names`() = runTest {
        // Given
        val maxName = "a".repeat(50)
        viewModel.onFirstNameChanged(maxName)
        viewModel.onLastNameChanged(maxName)
        coEvery { userRepository.registerUser(any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.onButtonClick()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { userRepository.registerUser(maxName, maxName) }
    }

    @Test
    fun `register navigates to Main on success`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John")
        viewModel.onLastNameChanged("Smith")
        coEvery { userRepository.registerUser(any(), any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.onButtonClick()
        advanceUntilIdle()

        // Then
        viewModel.events.test {
            val event = awaitItem()
            assertTrue(event is PersonalUiEvent.NavigateToMain)
        }
    }

    @Test
    fun `register shows error on failure`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John")
        viewModel.onLastNameChanged("Smith")
        coEvery { userRepository.registerUser(any(), any()) } returns
            AppResult.Err(AppError.NoInternet)

        // When
        viewModel.onButtonClick()
        testScheduler.runCurrent()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.error is PersonalError.Fields)
        val error = (state.error as PersonalError.Fields).message
        assertTrue(error is UiText.StringRes)
        assertEquals(com.ziopam.kollocol.core.common.R.string.error_no_internet,
            (error as UiText.StringRes).resId)
    }

    @Test
    fun `register clears error after 2 seconds`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John")
        viewModel.onLastNameChanged("Smith")
        coEvery { userRepository.registerUser(any(), any()) } returns
            AppResult.Err(AppError.Timeout)

        // When
        viewModel.onButtonClick()
        testScheduler.runCurrent()

        // Then
        val stateWithError = viewModel.uiState.value
        assertTrue(stateWithError.error is PersonalError.Fields)

        advanceTimeBy(2000L)
        testScheduler.runCurrent()
        
        val clearedState = viewModel.uiState.value
        assertNull(clearedState.error)
    }

    @Test
    fun `register does not clear error if it changed`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John")
        viewModel.onLastNameChanged("Smith")
        coEvery { userRepository.registerUser(any(), any()) } returns 
            AppResult.Err(AppError.Timeout)

        // When
        viewModel.onButtonClick()
        advanceUntilIdle()

        // Change error before 2 seconds
        viewModel.onAvatarError(UiText.StringRes(coreR.string.file_too_big))
        advanceTimeBy(2000L)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.error is PersonalError.Avatar)
        }
    }

    @Test
    fun `isButtonEnabled returns true when all conditions are met`() {
        // Given
        val state = PersonalUiState(
            firstName = "John",
            lastName = "Smith",
            error = null
        )

        // When
        val result = viewModel.isButtonEnabled(state)

        // Then
        assertTrue(result)
    }

    @Test
    fun `isButtonEnabled returns false when firstName is blank`() {
        // Given
        val state = PersonalUiState(
            firstName = "   ",
            lastName = "Smith",
            error = null
        )

        // When
        val result = viewModel.isButtonEnabled(state)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isButtonEnabled returns false when lastName is blank`() {
        // Given
        val state = PersonalUiState(
            firstName = "John",
            lastName = "",
            error = null
        )

        // When
        val result = viewModel.isButtonEnabled(state)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isButtonEnabled returns false when there is Fields error`() {
        // Given
        val state = PersonalUiState(
            firstName = "John",
            lastName = "Smith",
            error = PersonalError.Fields(UiText.StringRes(R.string.name_length_error, listOf(2, 50)))
        )

        // When
        val result = viewModel.isButtonEnabled(state)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isButtonEnabled returns true when there is Avatar error`() {
        // Given
        val state = PersonalUiState(
            firstName = "John",
            lastName = "Smith",
            error = PersonalError.Avatar(UiText.StringRes(coreR.string.file_too_big))
        )

        // When
        val result = viewModel.isButtonEnabled(state)

        // Then
        assertTrue(result)
    }

    @Test
    fun `sanitizeName handles multiple consecutive newlines`() = runTest {
        // When
        viewModel.onFirstNameChanged("John\n\n\nDoe")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John   Doe", state.firstName)
        }
    }

    @Test
    fun `sanitizeName handles mixed newlines and carriage returns`() = runTest {
        // When
        viewModel.onFirstNameChanged("John\r\n\n\rDoe")

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John    Doe", state.firstName)
        }
    }

    @Test
    fun `onButtonClick normalizes multiple spaces to single space`() = runTest {
        // Given
        viewModel.onFirstNameChanged("John     Doe")
        viewModel.onLastNameChanged("Smith")

        // When
        viewModel.onButtonClick()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("John Doe", state.firstName)
        }
    }
}