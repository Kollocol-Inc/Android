package com.ziopam.kollocol.feature.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ziopam.kollocol.R
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.ui.UiText
import com.ziopam.kollocol.domain.repository.AuthRepository
import com.ziopam.kollocol.domain.repository.VerifyResult
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk(relaxed = true)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEmailChanged filters whitespace characters`() = runTest {
        // When
        viewModel.onEmailChanged("test @exam ple.com")

        // Then
        viewModel.email.test {
            assertEquals("test@example.com", awaitItem())
        }
    }

    @Test
    fun `onEmailChanged filters non-ASCII characters`() = runTest {
        // When
        viewModel.onEmailChanged("test@примерcom")

        // Then
        viewModel.email.test {
            assertEquals("test@com", awaitItem())
        }
    }

    @Test
    fun `onEmailChanged limits length to 254 characters`() = runTest {
        // Given
        val longEmail = "a".repeat(300) + "@example.com"

        // When
        viewModel.onEmailChanged(longEmail)

        // Then
        viewModel.email.test {
            val result = awaitItem()
            assertEquals(254, result.length)
        }
    }

    @Test
    fun `onEmailChanged clears error`() = runTest {
        // Given
        viewModel.onEmailChanged("invalid")
        viewModel.requestCode()
        advanceUntilIdle()

        // When
        viewModel.onEmailChanged("test@example.com")

        // Then
        viewModel.currError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `requestCode does not proceed with invalid email`() = runTest {
        // Given
        viewModel.onEmailChanged("invalid-email")
        coEvery { authRepository.login(any()) } returns AppResult.Ok(Unit)

        // When
        viewModel.requestCode()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { authRepository.login(any()) }
        viewModel.currError.test {
            val error = awaitItem()
            assertTrue(error is UiText.StringRes)
            assertEquals(R.string.wrong_email_format, (error as UiText.StringRes).resId)
        }
    }

    @Test
    fun `requestCode proceeds with valid email`() = runTest {
        // Given
        val validEmail = "test@example.com"
        viewModel.onEmailChanged(validEmail)
        coEvery { authRepository.login(validEmail) } returns AppResult.Ok(Unit)

        // When
        viewModel.requestCode()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { authRepository.login(validEmail) }
    }

    @Test
    fun `requestCode sets loading state`() = runTest {
        // Given
        val validEmail = "test@example.com"
        viewModel.onEmailChanged(validEmail)
        coEvery { authRepository.login(validEmail) } returns AppResult.Ok(Unit)

        // When & Then
        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            viewModel.requestCode()
            assertEquals(true, awaitItem())
            
            advanceUntilIdle()
            
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `requestCode emits NavigateToCode on success`() = runTest {
        // Given
        val validEmail = "test@example.com"
        viewModel.onEmailChanged(validEmail)
        coEvery { authRepository.login(validEmail) } returns AppResult.Ok(Unit)

        // When
        viewModel.events.test {
            viewModel.requestCode()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is AuthUiEvent.NavigateToCode)
        }
    }

    @Test
    fun `requestCode shows BadRequest error as wrong email format`() = runTest {
        // Given
        val validEmail = "test@example.com"
        viewModel.onEmailChanged(validEmail)
        coEvery { authRepository.login(validEmail) } returns AppResult.Err(AppError.BadRequest("Invalid"))

        // When
        viewModel.requestCode()
        advanceUntilIdle()

        // Then
        viewModel.currError.test {
            val error = awaitItem()
            assertTrue(error is UiText.StringRes)
            assertEquals(R.string.wrong_email_format, (error as UiText.StringRes).resId)
        }
    }

    @Test
    fun `requestCode shows error for 2 seconds on non-BadRequest error`() = runTest {
        // Given
        val validEmail = "test@example.com"
        viewModel.onEmailChanged(validEmail)
        coEvery { authRepository.login(validEmail) } returns AppResult.Err(AppError.NoInternet)

        // When & Then
        viewModel.currError.test {
            assertEquals(null, awaitItem())
            viewModel.requestCode()
            
            advanceUntilIdle()
            
            val error = awaitItem()
            assertTrue(error is UiText.StringRes)
            assertEquals(R.string.error_no_internet, (error as UiText.StringRes).resId)

            advanceTimeBy(2000L)
            
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `isRequestCodeEnabled returns true when conditions are met`() {
        // Given
        viewModel.onEmailChanged("test@example.com")

        // When
        val result = viewModel.isRequestCodeEnabled()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isRequestCodeEnabled returns false when email is empty`() {
        // Given
        viewModel.onEmailChanged("")

        // When
        val result = viewModel.isRequestCodeEnabled()

        // Then
        assertFalse(result)
    }

    @Test
    fun `onCodeChanged filters non-digit characters`() = runTest {
        // When
        viewModel.onCodeChanged("12a3b4")

        // Then
        viewModel.code.test {
            assertEquals("1234", awaitItem())
        }
    }

    @Test
    fun `onCodeChanged limits length to 4 digits`() = runTest {
        // When
        viewModel.onCodeChanged("123456789")

        // Then
        viewModel.code.test {
            assertEquals("1234", awaitItem())
        }
    }

    @Test
    fun `verifyCode clears code after verification`() = runTest {
        // Given
        viewModel.onEmailChanged("test@example.com")
        viewModel.onCodeChanged("1234")
        coEvery { authRepository.verifyCode(any(), any()) } returns 
            AppResult.Ok(VerifyResult(isRegistered = true))

        // When
        viewModel.verifyCode()
        advanceUntilIdle()

        // Then
        viewModel.code.test {
            assertEquals("", awaitItem())
        }
    }

    @Test
    fun `verifyCode navigates to Main when user is registered`() = runTest {
        // Given
        viewModel.onEmailChanged("test@example.com")
        viewModel.onCodeChanged("1234")
        coEvery { authRepository.verifyCode(any(), any()) } returns 
            AppResult.Ok(VerifyResult(isRegistered = true))

        // When
        viewModel.events.test {
            viewModel.verifyCode()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is AuthUiEvent.NavigateToMain)
        }
    }

    @Test
    fun `verifyCode navigates to Personal when user is not registered`() = runTest {
        // Given
        viewModel.onEmailChanged("test@example.com")
        viewModel.onCodeChanged("1234")
        coEvery { authRepository.verifyCode(any(), any()) } returns 
            AppResult.Ok(VerifyResult(isRegistered = false))

        // When
        viewModel.events.test {
            viewModel.verifyCode()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is AuthUiEvent.NavigateToPersonal)
        }
    }

    @Test
    fun `verifyCode shows BadRequest error as wrong verification code`() = runTest {
        // Given
        viewModel.onEmailChanged("test@example.com")
        viewModel.onCodeChanged("1234")
        coEvery { authRepository.verifyCode(any(), any()) } returns 
            AppResult.Err(AppError.BadRequest("Invalid code"))

        // When
        viewModel.verifyCode()
        advanceUntilIdle()

        // Then
        viewModel.currError.test {
            val error = awaitItem()
            assertTrue(error is UiText.StringRes)
            assertEquals(R.string.wrong_verification_code, (error as UiText.StringRes).resId)
        }
    }

    @Test
    fun `verifyCode shows error for 2 seconds on non-BadRequest error`() = runTest {
        // Given
        viewModel.onEmailChanged("test@example.com")
        viewModel.onCodeChanged("1234")
        coEvery { authRepository.verifyCode(any(), any()) } returns
            AppResult.Err(AppError.Timeout)

        // When & Then
        viewModel.currError.test {
            assertEquals(null, awaitItem())
            viewModel.verifyCode()
            advanceUntilIdle()
            
            val error = awaitItem()
            assertTrue(error is UiText.StringRes)
            assertEquals(R.string.error_timeout, (error as UiText.StringRes).resId)

            advanceTimeBy(2000L)
            
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `verifyCode keeps loading false on error`() = runTest {
        // Given
        viewModel.onEmailChanged("test@example.com")
        viewModel.onCodeChanged("1234")
        coEvery { authRepository.verifyCode(any(), any()) } returns 
            AppResult.Err(AppError.BadRequest("Invalid"))

        // When
        viewModel.verifyCode()
        advanceUntilIdle()

        // Then
        viewModel.isLoading.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `clearError clears current error`() = runTest {
        // Given
        viewModel.onEmailChanged("invalid")
        viewModel.requestCode()
        advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.currError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `email validation accepts valid email addresses`() = runTest {
        // Given
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.co.uk",
            "user+tag@example.com"
        )

        validEmails.forEach { email ->
            // When
            viewModel.onEmailChanged(email)
            viewModel.requestCode()
            advanceUntilIdle()

            // Then
            coEvery { authRepository.login(email) } returns AppResult.Ok(Unit)
            coVerify { authRepository.login(email) }
        }
    }

    @Test
    fun `email validation rejects invalid email addresses`() = runTest {
        // Given
        val invalidEmails = listOf(
            "invalid",
            "@example.com",
            "user@",
            "user name@example.com"
        )

        invalidEmails.forEach { email ->
            // When
            viewModel.onEmailChanged(email)
            viewModel.requestCode()
            advanceUntilIdle()

            // Then
            viewModel.currError.test {
                val error = awaitItem()
                assertTrue(error is UiText.StringRes)
                assertEquals(R.string.wrong_email_format, (error as UiText.StringRes).resId)
            }
        }
    }
}