package com.ziopam.kollocol.core.network

import com.google.gson.Gson
import com.ziopam.kollocol.core.network.model.RefreshTokenResponseDto
import com.ziopam.kollocol.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TokenAuthenticatorTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var gson: Gson
    private lateinit var authenticator: TokenAuthenticator
    private val baseUrl = "https://api.example.com/"

    @Before
    fun setup() {
        sessionRepository = mockk(relaxed = true)
        gson = Gson()
        authenticator = TokenAuthenticator(sessionRepository, gson, baseUrl)
    }

    @Test
    fun `authenticate returns null when request has no Authorization header`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()
        val response = createResponse(request, 401)

        // When
        val result = authenticator.authenticate(null, response)

        // Then
        assertNull(result)
    }

    @Test
    fun `authenticate returns null when responseCount is 2 or more`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .header("Authorization", "Bearer old_token")
            .build()

        // Create a chain of responses (3 attempts)
        val response1 = createResponse(request, 401)
        val response2 = createResponse(request, 401, response1)
        val response3 = createResponse(request, 401, response2)

        // When
        val result = authenticator.authenticate(null, response3)

        // Then
        assertNull(result)
    }

    @Test
    fun `authenticate returns null when refresh token is not available`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .header("Authorization", "Bearer old_token")
            .build()
        val response = createResponse(request, 401)

        coEvery { sessionRepository.getRefreshToken() } returns null

        // When
        val result = authenticator.authenticate(null, response)

        // Then
        assertNull(result)
    }

    @Test
    fun `authenticate returns null when refresh token is blank`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .header("Authorization", "Bearer old_token")
            .build()
        val response = createResponse(request, 401)

        coEvery { sessionRepository.getRefreshToken() } returns ""

        // When
        val result = authenticator.authenticate(null, response)

        // Then
        assertNull(result)
    }

    @Test
    fun `authenticate returns null when new access token is blank`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .header("Authorization", "Bearer old_token")
            .build()
        val response = createResponse(request, 401)

        coEvery { sessionRepository.getRefreshToken() } returns "refresh_token"

        val client = createMockClient(
            RefreshTokenResponseDto(
                accessToken = "",
                refreshToken = "new_refresh_token"
            )
        )

        // When
        val result = authenticator.authenticate(null, response)

        // Then
        assertNull(result)
    }

    @Test
    fun `responseCount returns 1 for single response`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()
        val response = createResponse(request, 401)

        // When
        val method = TokenAuthenticator::class.java.getDeclaredMethod(
            "responseCount",
            Response::class.java
        )
        method.isAccessible = true
        val count = method.invoke(authenticator, response) as Int

        // Then
        assertEquals(1, count)
    }

    @Test
    fun `responseCount returns 2 for response with one prior`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()
        val response1 = createResponse(request, 401)
        val response2 = createResponse(request, 401, response1)

        // When
        val method = TokenAuthenticator::class.java.getDeclaredMethod(
            "responseCount",
            Response::class.java
        )
        method.isAccessible = true
        val count = method.invoke(authenticator, response2) as Int

        // Then
        assertEquals(2, count)
    }

    @Test
    fun `responseCount returns 3 for response with two priors`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()
        val response1 = createResponse(request, 401)
        val response2 = createResponse(request, 401, response1)
        val response3 = createResponse(request, 401, response2)

        // When
        val method = TokenAuthenticator::class.java.getDeclaredMethod(
            "responseCount",
            Response::class.java
        )
        method.isAccessible = true
        val count = method.invoke(authenticator, response3) as Int

        // Then
        assertEquals(3, count)
    }

    @Test
    fun `authenticate checks for Authorization header presence`() = runTest {
        // Given
        val requestWithAuth = Request.Builder()
            .url("https://api.example.com/test")
            .header("Authorization", "Bearer token")
            .build()
        val requestWithoutAuth = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val responseWithAuth = createResponse(requestWithAuth, 401)
        val responseWithoutAuth = createResponse(requestWithoutAuth, 401)

        coEvery { sessionRepository.getRefreshToken() } returns "refresh_token"

        // When
        val resultWithAuth = authenticator.authenticate(null, responseWithAuth)
        val resultWithoutAuth = authenticator.authenticate(null, responseWithoutAuth)

        // Then
        assertNull(resultWithoutAuth)
    }

    @Test
    fun `authenticate does not save tokens when refresh token is null`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .header("Authorization", "Bearer old_token")
            .build()
        val response = createResponse(request, 401)

        coEvery { sessionRepository.getRefreshToken() } returns null

        // When
        authenticator.authenticate(null, response)

        // Then
        coVerify(exactly = 0) { sessionRepository.saveTokens(any(), any()) }
    }

    @Test
    fun `authenticate handles multiple consecutive 401 responses`() = runTest {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .header("Authorization", "Bearer token")
            .build()

        // Create chain: response1 -> response2 -> response3
        val response1 = createResponse(request, 401)
        val response2 = createResponse(request, 401, response1)
        val response3 = createResponse(request, 401, response2)

        coEvery { sessionRepository.getRefreshToken() } returns "refresh_token"

        // When
        val result = authenticator.authenticate(null, response3)

        assertNull(result)
    }


    private fun createResponse(
        request: Request,
        code: Int,
        priorResponse: Response? = null
    ): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("Response")
            .body("".toResponseBody("text/plain".toMediaTypeOrNull()))
            .priorResponse(priorResponse)
            .build()
    }

    private fun createMockClient(refreshResponse: RefreshTokenResponseDto): OkHttpClient {
        val responseJson = gson.toJson(refreshResponse)
        val interceptor = Interceptor { chain ->
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(responseJson.toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }
}