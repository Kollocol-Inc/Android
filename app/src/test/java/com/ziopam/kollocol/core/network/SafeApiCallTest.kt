package com.ziopam.kollocol.core.network

import com.google.gson.Gson
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class SafeApiCallTest {

    private lateinit var safeApiCall: SafeApiCall
    private val gson = Gson()

    @Before
    fun setup() {
        safeApiCall = SafeApiCall(gson)
    }

    @Test
    fun `call returns Ok when block succeeds`() = runTest {
        // Given
        val expectedValue = "success"

        // When
        val result = safeApiCall.call { expectedValue }

        // Then
        assertTrue(result is AppResult.Ok)
        assertEquals(expectedValue, (result as AppResult.Ok).value)
    }

    @Test
    fun `call returns BadRequest error for 400 status code`() = runTest {
        // Given
        val errorMessage = "Invalid request"
        val errorBody = """{"message":"$errorMessage"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(400, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        val error = (result as AppResult.Err).error
        assertTrue(error is AppError.BadRequest)
        assertEquals(errorMessage, (error as AppError.BadRequest).message)
    }

    @Test
    fun `call returns Unauthorized error for 401 status code`() = runTest {
        // Given
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(401, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.Unauthorized)
    }

    @Test
    fun `call returns Forbidden error for 403 status code`() = runTest {
        // Given
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(403, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.Forbidden)
    }

    @Test
    fun `call returns NotFound error for 404 status code`() = runTest {
        // Given
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(404, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.NotFound)
    }

    @Test
    fun `call returns TooManyRequests error for 429 status code`() = runTest {
        // Given
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(429, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.TooManyRequests)
    }

    @Test
    fun `call returns ServerError for 500 status code`() = runTest {
        // Given
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(500, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.ServerError)
    }

    @Test
    fun `call returns Unknown error for unhandled HTTP status code`() = runTest {
        // Given
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(418, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        val error = (result as AppResult.Err).error
        assertTrue(error is AppError.Unknown)
        assertEquals("HTTP 418", (error as AppError.Unknown).message)
    }

    @Test
    fun `call parses error message from response body`() = runTest {
        // Given
        val errorMessage = "Custom error message"
        val errorBody = """{"message":"$errorMessage"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(418, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        val error = (result as AppResult.Err).error
        assertTrue(error is AppError.Unknown)
        assertEquals(errorMessage, (error as AppError.Unknown).message)
    }

    @Test
    fun `call returns Timeout error for SocketTimeoutException`() = runTest {
        // When
        val result = safeApiCall.call<String> { throw SocketTimeoutException() }

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.Timeout)
    }

    @Test
    fun `call returns NoInternet error for IOException`() = runTest {
        // When
        val result = safeApiCall.call<String> { throw IOException() }

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.NoInternet)
    }

    @Test
    fun `call returns Unknown error for generic Exception`() = runTest {
        // Given
        val exceptionMessage = "Something went wrong"

        // When
        val result = safeApiCall.call<String> { throw RuntimeException(exceptionMessage) }

        // Then
        assertTrue(result is AppResult.Err)
        val error = (result as AppResult.Err).error
        assertTrue(error is AppError.Unknown)
        assertEquals(exceptionMessage, (error as AppError.Unknown).message)
    }

    @Test
    fun `call handles null error body gracefully`() = runTest {
        // Given
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(400, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        val error = (result as AppResult.Err).error
        assertTrue(error is AppError.BadRequest)
        assertEquals(null, (error as AppError.BadRequest).message)
    }

    @Test
    fun `call handles malformed JSON in error body`() = runTest {
        // Given
        val errorBody = "not a json"
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(418, errorBody)
        val exception = HttpException(response)

        // When
        val result = safeApiCall.call<String> { throw exception }

        // Then
        assertTrue(result is AppResult.Err)
        val error = (result as AppResult.Err).error
        assertTrue(error is AppError.Unknown)
        assertEquals("HTTP 418", (error as AppError.Unknown).message)
    }
}