package com.ziopam.kollocol.core.session

import com.ziopam.kollocol.domain.repository.SessionState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionRepositoryImplTest {

    private lateinit var sessionDataStore: SessionDataStore
    private lateinit var sessionRepository: SessionRepositoryImpl
    private lateinit var tokensFlow: MutableStateFlow<SessionTokens>

    @Before
    fun setup() {
        sessionDataStore = mockk(relaxed = true)
        tokensFlow = MutableStateFlow(SessionTokens(null, null))
        coEvery { sessionDataStore.tokensFlow } returns tokensFlow
        sessionRepository = SessionRepositoryImpl(sessionDataStore)
    }

    @Test
    fun `sessionState returns Authorized when both tokens are present`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "access_token",
            refreshToken = "refresh_token"
        )

        // When
        val state = sessionRepository.sessionState.first()

        // Then
        assertTrue(state is SessionState.Authorized)
    }

    @Test
    fun `sessionState returns Unauthorized when access token is null`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = null,
            refreshToken = "refresh_token"
        )

        // When
        val state = sessionRepository.sessionState.first()

        // Then
        assertTrue(state is SessionState.Unauthorized)
    }

    @Test
    fun `sessionState returns Unauthorized when refresh token is null`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "access_token",
            refreshToken = null
        )

        // When
        val state = sessionRepository.sessionState.first()

        // Then
        assertTrue(state is SessionState.Unauthorized)
    }

    @Test
    fun `sessionState returns Unauthorized when both tokens are null`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = null,
            refreshToken = null
        )

        // When
        val state = sessionRepository.sessionState.first()

        // Then
        assertTrue(state is SessionState.Unauthorized)
    }

    @Test
    fun `sessionState returns Unauthorized when access token is blank`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "   ",
            refreshToken = "refresh_token"
        )

        // When
        val state = sessionRepository.sessionState.first()

        // Then
        assertTrue(state is SessionState.Unauthorized)
    }

    @Test
    fun `sessionState returns Unauthorized when refresh token is blank`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "access_token",
            refreshToken = ""
        )

        // When
        val state = sessionRepository.sessionState.first()

        // Then
        assertTrue(state is SessionState.Unauthorized)
    }

    @Test
    fun `hasValidSession returns true when both tokens are present`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "access_token",
            refreshToken = "refresh_token"
        )

        // When
        val result = sessionRepository.hasValidSession()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasValidSession returns false when access token is null`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = null,
            refreshToken = "refresh_token"
        )

        // When
        val result = sessionRepository.hasValidSession()

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasValidSession returns false when refresh token is null`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "access_token",
            refreshToken = null
        )

        // When
        val result = sessionRepository.hasValidSession()

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasValidSession returns false when tokens are blank`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "",
            refreshToken = "   "
        )

        // When
        val result = sessionRepository.hasValidSession()

        // Then
        assertFalse(result)
    }

    @Test
    fun `saveTokens calls dataStore saveTokens`() = runTest {
        // Given
        val accessToken = "new_access_token"
        val refreshToken = "new_refresh_token"

        // When
        sessionRepository.saveTokens(accessToken, refreshToken)

        // Then
        coVerify(exactly = 1) {
            sessionDataStore.saveTokens(accessToken, refreshToken)
        }
    }

    @Test
    fun `clearSession calls dataStore clear`() = runTest {
        // When
        sessionRepository.clearSession()

        // Then
        coVerify(exactly = 1) {
            sessionDataStore.clear()
        }
    }

    @Test
    fun `getAccessToken returns access token from dataStore`() = runTest {
        // Given
        val expectedToken = "access_token_123"
        tokensFlow.value = SessionTokens(
            accessToken = expectedToken,
            refreshToken = "refresh_token"
        )

        // When
        val result = sessionRepository.getAccessToken()

        // Then
        assertEquals(expectedToken, result)
    }

    @Test
    fun `getAccessToken returns null when token is not present`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = null,
            refreshToken = "refresh_token"
        )

        // When
        val result = sessionRepository.getAccessToken()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `getRefreshToken returns refresh token from dataStore`() = runTest {
        // Given
        val expectedToken = "refresh_token_456"
        tokensFlow.value = SessionTokens(
            accessToken = "access_token",
            refreshToken = expectedToken
        )

        // When
        val result = sessionRepository.getRefreshToken()

        // Then
        assertEquals(expectedToken, result)
    }

    @Test
    fun `getRefreshToken returns null when token is not present`() = runTest {
        // Given
        tokensFlow.value = SessionTokens(
            accessToken = "access_token",
            refreshToken = null
        )

        // When
        val result = sessionRepository.getRefreshToken()

        // Then
        assertEquals(null, result)
    }
}