package com.ziopam.kollocol.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.user.GetUserResponseDto
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.data.storage.datastore.UserDataStoreKeys
import com.ziopam.kollocol.domain.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PersonalRepositoryImplTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var safeApiCall: SafeApiCall
    private lateinit var api: UserApi
    private lateinit var repository: PersonalRepositoryImpl

    @Before
    fun setup() {
        dataStore = mockk(relaxed = true)
        safeApiCall = mockk(relaxed = true)
        api = mockk(relaxed = true)
        repository = PersonalRepositoryImpl(dataStore, safeApiCall, api)
    }

    @Test
    fun `getUser returns Ok with user data when API succeeds`() = runTest {
        // Given
        val dto = GetUserResponseDto(firstName = "John", lastName = "Doe", avatarUrl = null)
        coEvery { safeApiCall.call<GetUserResponseDto>(any()) } returns AppResult.Ok(dto)

        // When
        val result = repository.getUser()

        // Then
        assertTrue(result is AppResult.Ok)
        val user = (result as AppResult.Ok).value
        assertEquals("John", user.firstName)
        assertEquals("Doe", user.lastName)
    }

    @Test
    fun `getUser calls updateData when API succeeds`() = runTest {
        // Given
        val dto = GetUserResponseDto(firstName = "John", lastName = "Doe", avatarUrl = null)
        coEvery { safeApiCall.call<GetUserResponseDto>(any()) } returns AppResult.Ok(dto)

        // When
        repository.getUser()

        // Then
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `getUser returns cached user when API fails and cache has firstName and lastName`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetUserResponseDto>(any()) } returns AppResult.Err(AppError.NoInternet)
        val prefs = mockk<Preferences>()
        every { prefs[UserDataStoreKeys.FIRST_NAME] } returns "Cached"
        every { prefs[UserDataStoreKeys.LAST_NAME] } returns "Name"
        every { prefs[UserDataStoreKeys.AVATAR_URL] } returns null
        every { dataStore.data } returns flowOf(prefs)

        // When
        val result = repository.getUser()

        // Then
        assertTrue(result is AppResult.Ok)
        val user = (result as AppResult.Ok).value
        assertEquals("Cached", user.firstName)
        assertEquals("Name", user.lastName)
    }

    @Test
    fun `getUser returns cached avatarUrl when API fails and cache has it`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetUserResponseDto>(any()) } returns AppResult.Err(AppError.Timeout)
        val prefs = mockk<Preferences>()
        every { prefs[UserDataStoreKeys.FIRST_NAME] } returns "Jane"
        every { prefs[UserDataStoreKeys.LAST_NAME] } returns "Doe"
        every { prefs[UserDataStoreKeys.AVATAR_URL] } returns "https://example.com/avatar.png"
        every { dataStore.data } returns flowOf(prefs)

        // When
        val result = repository.getUser()

        // Then
        assertTrue(result is AppResult.Ok)
        assertEquals("https://example.com/avatar.png", (result as AppResult.Ok).value.avatarUrl)
    }

    @Test
    fun `getUser returns Err when API fails and cache has no firstName`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetUserResponseDto>(any()) } returns AppResult.Err(AppError.NoInternet)
        val prefs = mockk<Preferences>()
        every { prefs[UserDataStoreKeys.FIRST_NAME] } returns null
        every { prefs[UserDataStoreKeys.LAST_NAME] } returns "Doe"
        every { prefs[UserDataStoreKeys.AVATAR_URL] } returns null
        every { dataStore.data } returns flowOf(prefs)

        // When
        val result = repository.getUser()

        // Then
        assertTrue(result is AppResult.Err)
    }

    @Test
    fun `getUser returns Err when API fails and cache has no lastName`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetUserResponseDto>(any()) } returns AppResult.Err(AppError.ServerError)
        val prefs = mockk<Preferences>()
        every { prefs[UserDataStoreKeys.FIRST_NAME] } returns "John"
        every { prefs[UserDataStoreKeys.LAST_NAME] } returns null
        every { prefs[UserDataStoreKeys.AVATAR_URL] } returns null
        every { dataStore.data } returns flowOf(prefs)

        // When
        val result = repository.getUser()

        // Then
        assertTrue(result is AppResult.Err)
    }

    @Test
    fun `getUser returns Err when API fails and cache is empty`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetUserResponseDto>(any()) } returns AppResult.Err(AppError.Forbidden)
        val prefs = mockk<Preferences>()
        every { prefs[UserDataStoreKeys.FIRST_NAME] } returns null
        every { prefs[UserDataStoreKeys.LAST_NAME] } returns null
        every { prefs[UserDataStoreKeys.AVATAR_URL] } returns null
        every { dataStore.data } returns flowOf(prefs)

        // When
        val result = repository.getUser()

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.Forbidden)
    }

    @Test
    fun `updateUser calls dataStore edit`() = runTest {
        // Given
        val user = User(firstName = "Alice", lastName = "Wonder", avatarUrl = null)

        // When
        repository.updateUser(user)

        // Then
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `clear calls dataStore edit`() = runTest {
        // When
        repository.clear()

        // Then
        coVerify { dataStore.updateData(any()) }
    }
}
