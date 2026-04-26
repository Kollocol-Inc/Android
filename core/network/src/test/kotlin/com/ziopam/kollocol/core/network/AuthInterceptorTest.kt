package com.ziopam.kollocol.core.network

import com.ziopam.kollocol.domain.repository.SessionRepository
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var interceptor: AuthInterceptor

    private fun buildChain(url: String = "https://api.example.com/test"): Pair<Interceptor.Chain, CapturingSlot<Request>> {
        val chain = mockk<Interceptor.Chain>()
        val request = Request.Builder().url(url).build()
        val capturedRequest = slot<Request>()

        every { chain.request() } returns request
        every { chain.proceed(capture(capturedRequest)) } returns Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("".toResponseBody())
            .build()

        return chain to capturedRequest
    }

    @Before
    fun setup() {
        sessionRepository = mockk()
        interceptor = AuthInterceptor(sessionRepository)
    }

    @Test
    fun `intercept adds Authorization header when token is present`() = runTest {
        // Given
        coEvery { sessionRepository.getAccessToken() } returns "valid_token"
        val (chain, capturedRequest) = buildChain()

        // When
        interceptor.intercept(chain)

        // Then
        assertEquals("Bearer valid_token", capturedRequest.captured.header("Authorization"))
    }

    @Test
    fun `intercept does not add Authorization header when token is null`() = runTest {
        // Given
        coEvery { sessionRepository.getAccessToken() } returns null
        val (chain, capturedRequest) = buildChain()

        // When
        interceptor.intercept(chain)

        // Then
        assertNull(capturedRequest.captured.header("Authorization"))
    }

    @Test
    fun `intercept does not add Authorization header when token is blank`() = runTest {
        // Given
        coEvery { sessionRepository.getAccessToken() } returns "   "
        val (chain, capturedRequest) = buildChain()

        // When
        interceptor.intercept(chain)

        // Then
        assertNull(capturedRequest.captured.header("Authorization"))
    }

    @Test
    fun `intercept does not add Authorization header when token is empty`() = runTest {
        // Given
        coEvery { sessionRepository.getAccessToken() } returns ""
        val (chain, capturedRequest) = buildChain()

        // When
        interceptor.intercept(chain)

        // Then
        assertNull(capturedRequest.captured.header("Authorization"))
    }

    @Test
    fun `intercept always calls chain proceed`() = runTest {
        // Given
        coEvery { sessionRepository.getAccessToken() } returns null
        val (chain, _) = buildChain()

        // When
        interceptor.intercept(chain)

        // Then
        io.mockk.verify { chain.proceed(any()) }
    }

    @Test
    fun `intercept returns response from chain`() = runTest {
        // Given
        coEvery { sessionRepository.getAccessToken() } returns "token"
        val (chain, _) = buildChain()

        // When
        val response = interceptor.intercept(chain)

        // Then
        assertEquals(200, response.code)
    }
}
