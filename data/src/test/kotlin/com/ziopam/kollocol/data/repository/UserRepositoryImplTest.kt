package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.user.RegisterUserResponseDto
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.domain.repository.PersonalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    private lateinit var api: UserApi
    private lateinit var safeApiCall: SafeApiCall
    private lateinit var personalRepository: PersonalRepository
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        api = mockk(relaxed = true)
        safeApiCall = mockk(relaxed = true)
        personalRepository = mockk(relaxed = true)
        repository = UserRepositoryImpl(api, safeApiCall, personalRepository)
    }

    @Test
    fun `registerUser returns Ok on success`() = runTest {
        // Given
        val responseDto = RegisterUserResponseDto(firstName = "John", lastName = "Doe", avatarUrl = null)
        coEvery { safeApiCall.call<RegisterUserResponseDto>(any()) } returns AppResult.Ok(responseDto)

        // When
        val result = repository.registerUser("John", "Doe")

        // Then
        assertTrue(result is AppResult.Ok)
    }

    @Test
    fun `registerUser calls personalRepository updateUser on success`() = runTest {
        // Given
        val responseDto = RegisterUserResponseDto(firstName = "John", lastName = "Doe", avatarUrl = "https://example.com/avatar.png")
        coEvery { safeApiCall.call<RegisterUserResponseDto>(any()) } returns AppResult.Ok(responseDto)

        // When
        repository.registerUser("John", "Doe")

        // Then
        coVerify { personalRepository.updateUser(any()) }
    }

    @Test
    fun `registerUser updateUser receives correct user data`() = runTest {
        // Given
        val responseDto = RegisterUserResponseDto(firstName = "Alice", lastName = "Smith", avatarUrl = null)
        coEvery { safeApiCall.call<RegisterUserResponseDto>(any()) } returns AppResult.Ok(responseDto)

        // When
        repository.registerUser("Alice", "Smith")

        // Then
        coVerify { personalRepository.updateUser(match { it.firstName == "Alice" && it.lastName == "Smith" }) }
    }

    @Test
    fun `registerUser returns Err on API failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<RegisterUserResponseDto>(any()) } returns AppResult.Err(AppError.BadRequest("Invalid data"))

        // When
        val result = repository.registerUser("John", "Doe")

        // Then
        assertTrue(result is AppResult.Err)
    }

    @Test
    fun `registerUser does not call personalRepository updateUser on failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<RegisterUserResponseDto>(any()) } returns AppResult.Err(AppError.ServerError)

        // When
        repository.registerUser("John", "Doe")

        // Then
        coVerify(exactly = 0) { personalRepository.updateUser(any()) }
    }

    @Test
    fun `registerUser propagates error type on failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<RegisterUserResponseDto>(any()) } returns AppResult.Err(AppError.NoInternet)

        // When
        val result = repository.registerUser("John", "Doe")

        // Then
        assertTrue((result as AppResult.Err).error is AppError.NoInternet)
    }
}
