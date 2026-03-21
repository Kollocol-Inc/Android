package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.auth.AuthApi
import com.ziopam.kollocol.data.datasource.remote.auth.LoginRequestDto
import com.ziopam.kollocol.data.datasource.remote.auth.VerifyCodeRequestDto
import com.ziopam.kollocol.data.datasource.remote.auth.VerifyCodeResponseDto
import com.ziopam.kollocol.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var authApi: AuthApi
    private lateinit var safeApiCall: SafeApiCall
    private lateinit var sessionRepository: SessionRepository
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setup() {
        authApi = mockk(relaxed = true)
        safeApiCall = mockk(relaxed = true)
        sessionRepository = mockk(relaxed = true)
        authRepository = AuthRepositoryImpl(authApi, safeApiCall, sessionRepository)
    }

    @Test
    fun `login calls API with correct email`() = runTest {
        // Given
        val email = "test@example.com"
        val requestSlot = slot<suspend () -> Unit>()
        coEvery { safeApiCall.call(capture(requestSlot)) } coAnswers {
            requestSlot.captured.invoke()
            AppResult.Ok(Unit)
        }

        // When
        authRepository.login(email)

        // Then
        coVerify(exactly = 1) { authApi.login(LoginRequestDto(email)) }
    }

    @Test
    fun `login returns result from safeApiCall`() = runTest {
        // Given
        val email = "test@example.com"
        coEvery { safeApiCall.call<Unit>(any()) } returns AppResult.Ok(Unit)

        // When
        val result = authRepository.login(email)

        // Then
        assertTrue(result is AppResult.Ok)
    }

    @Test
    fun `login returns error from safeApiCall`() = runTest {
        // Given
        val email = "test@example.com"
        coEvery { safeApiCall.call<Unit>(any()) } returns AppResult.Err(AppError.NoInternet)

        // When
        val result = authRepository.login(email)

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.NoInternet)
    }

    @Test
    fun `resendCode calls API with correct email`() = runTest {
        // Given
        val email = "test@example.com"
        val requestSlot = slot<suspend () -> Unit>()
        coEvery { safeApiCall.call(capture(requestSlot)) } coAnswers {
            requestSlot.captured.invoke()
            AppResult.Ok(Unit)
        }

        // When
        authRepository.resendCode(email)

        // Then
        coVerify(exactly = 1) { authApi.resendCode(LoginRequestDto(email)) }
    }

    @Test
    fun `verifyCode calls API with correct parameters`() = runTest {
        // Given
        val email = "test@example.com"
        val code = "1234"
        val responseDto = VerifyCodeResponseDto(
            accessToken = "access_token",
            refreshToken = "refresh_token",
            isRegistered = true
        )
        
        coEvery { authApi.verify(VerifyCodeRequestDto(code, email)) } returns responseDto
        coEvery { safeApiCall.call<Any>(any()) } coAnswers {
            val block = firstArg<suspend () -> Any>()
            AppResult.Ok(block.invoke())
        }

        // When
        authRepository.verifyCode(email, code)

        // Then
        coVerify(exactly = 1) { authApi.verify(VerifyCodeRequestDto(code, email)) }
    }

    @Test
    fun `verifyCode saves tokens to session repository`() = runTest {
        // Given
        val email = "test@example.com"
        val code = "1234"
        val accessToken = "access_token_123"
        val refreshToken = "refresh_token_456"
        val responseDto = VerifyCodeResponseDto(
            accessToken = accessToken,
            refreshToken = refreshToken,
            isRegistered = true
        )

        coEvery { authApi.verify(any()) } returns responseDto
        coEvery { safeApiCall.call<Any>(any()) } coAnswers {
            val block = firstArg<suspend () -> Any>()
            AppResult.Ok(block.invoke())
        }

        // When
        authRepository.verifyCode(email, code)

        // Then
        coVerify(exactly = 1) { sessionRepository.saveTokens(accessToken, refreshToken) }
    }

    @Test
    fun `verifyCode returns VerifyResult with isRegistered true`() = runTest {
        // Given
        val email = "test@example.com"
        val code = "1234"
        val responseDto = VerifyCodeResponseDto(
            accessToken = "access",
            refreshToken = "refresh",
            isRegistered = true
        )

        coEvery { authApi.verify(any()) } returns responseDto
        coEvery { safeApiCall.call<Any>(any()) } coAnswers {
            val block = firstArg<suspend () -> Any>()
            AppResult.Ok(block.invoke())
        }

        // When
        val result = authRepository.verifyCode(email, code)

        // Then
        assertTrue(result is AppResult.Ok)
        assertTrue((result as AppResult.Ok).value.isRegistered)
    }

    @Test
    fun `verifyCode returns VerifyResult with isRegistered false`() = runTest {
        // Given
        val email = "test@example.com"
        val code = "1234"
        val responseDto = VerifyCodeResponseDto(
            accessToken = "access",
            refreshToken = "refresh",
            isRegistered = false
        )

        coEvery { authApi.verify(any()) } returns responseDto
        coEvery { safeApiCall.call<Any>(any()) } coAnswers {
            val block = firstArg<suspend () -> Any>()
            AppResult.Ok(block.invoke())
        }

        // When
        val result = authRepository.verifyCode(email, code)

        // Then
        assertTrue(result is AppResult.Ok)
        assertFalse((result as AppResult.Ok).value.isRegistered)
    }

    @Test
    fun `verifyCode handles null isRegistered as false`() = runTest {
        // Given
        val email = "test@example.com"
        val code = "1234"
        val responseDto = VerifyCodeResponseDto(
            accessToken = "access",
            refreshToken = "refresh",
            isRegistered = null
        )

        coEvery { authApi.verify(any()) } returns responseDto
        coEvery { safeApiCall.call<Any>(any()) } coAnswers {
            val block = firstArg<suspend () -> Any>()
            AppResult.Ok(block.invoke())
        }

        // When
        val result = authRepository.verifyCode(email, code)

        // Then
        assertTrue(result is AppResult.Ok)
        assertFalse((result as AppResult.Ok).value.isRegistered)
    }

    @Test
    fun `verifyCode saves empty tokens when response tokens are null`() = runTest {
        // Given
        val email = "test@example.com"
        val code = "1234"
        val responseDto = VerifyCodeResponseDto(
            accessToken = null,
            refreshToken = null,
            isRegistered = true
        )

        coEvery { authApi.verify(any()) } returns responseDto
        coEvery { safeApiCall.call<Any>(any()) } coAnswers {
            val block = firstArg<suspend () -> Any>()
            AppResult.Ok(block.invoke())
        }

        // When
        authRepository.verifyCode(email, code)

        // Then
        coVerify(exactly = 1) { sessionRepository.saveTokens("", "") }
    }
}