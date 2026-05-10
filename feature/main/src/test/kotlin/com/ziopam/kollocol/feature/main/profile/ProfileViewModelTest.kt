package com.ziopam.kollocol.feature.main.profile

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.NotificationSettings
import com.ziopam.kollocol.domain.model.ThemeMode
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.AuthRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
import com.ziopam.kollocol.domain.repository.SessionRepository
import com.ziopam.kollocol.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var userRepository: UserRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var personalRepository: PersonalRepository
    private lateinit var sessionRepository: SessionRepository
    private lateinit var viewModel: ProfileViewModel

    private val userFlow = MutableStateFlow(
        User(avatarUrl = null, firstName = "John", lastName = "Doe", email = "john@example.com")
    )
    private val themeModeFlow = MutableStateFlow(ThemeMode.LIGHT)

    private val defaultNotifSettings = NotificationSettings(
        newQuizzes = true,
        quizResults = true,
        groupInvites = false,
        deadlineReminder = "1day"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk(relaxed = true)
        authRepository = mockk(relaxed = true)
        personalRepository = mockk(relaxed = true)
        sessionRepository = mockk(relaxed = true)

        every { personalRepository.getUserFlow() } returns userFlow
        every { personalRepository.getThemeMode() } returns themeModeFlow

        viewModel = ProfileViewModel(userRepository, authRepository, personalRepository, sessionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads user data from getUserFlow`() = runTest {
        // Given
        backgroundScope.launch { viewModel.state.collect {} }

        // When
        advanceUntilIdle()

        // Then
        assertEquals("John", viewModel.state.value.firstName)
        assertEquals("Doe", viewModel.state.value.lastName)
        assertEquals("john@example.com", viewModel.state.value.email)
    }

    @Test
    fun `init loads theme mode from getThemeMode`() = runTest {
        // Given
        themeModeFlow.value = ThemeMode.DARK
        viewModel = ProfileViewModel(userRepository, authRepository, personalRepository, sessionRepository)
        backgroundScope.launch { viewModel.state.collect {} }

        // When
        advanceUntilIdle()

        // Then
        assertEquals(ThemeMode.DARK, viewModel.state.value.themeMode)
    }

    @Test
    fun `init sets avatarUrl from getUserFlow when not null`() = runTest {
        // Given
        userFlow.value = User(avatarUrl = "http://example.com/avatar.png", firstName = "John", lastName = "Doe", email = "")
        viewModel = ProfileViewModel(userRepository, authRepository, personalRepository, sessionRepository)
        backgroundScope.launch { viewModel.state.collect {} }

        // When
        advanceUntilIdle()

        // Then
        assertEquals("http://example.com/avatar.png", viewModel.state.value.avatarUrl)
    }

    @Test
    fun `init sets avatarUrl to null when user avatarUrl is null`() = runTest {
        // Given
        backgroundScope.launch { viewModel.state.collect {} }

        // When
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.avatarUrl)
    }

    @Test
    fun `refresh sets isLoading to false after completion`() = runTest {
        // Given
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `refresh updates notificationSettings on success`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Ok(defaultNotifSettings)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        assertEquals(defaultNotifSettings, viewModel.state.value.notificationSettings)
    }

    @Test
    fun `refresh sends ShowError event when getUser fails`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Err(AppError.NoInternet)

        // When
        viewModel.events.test {
            viewModel.refresh()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is ProfileEvent.ShowError)
        }
    }

    @Test
    fun `refresh does not update notificationSettings when getNotificationSettings fails`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Err(AppError.ServerError)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.notificationSettings)
    }

    @Test
    fun `onThemeChange calls setThemeMode with given mode`() = runTest {
        // When
        viewModel.onThemeChange(ThemeMode.DARK)
        advanceUntilIdle()

        // Then
        coVerify { personalRepository.setThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun `onNewQuizzesToggle updates notificationSettings newQuizzes`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Ok(defaultNotifSettings)
        coEvery { userRepository.updateNotificationSettings(any()) } returns AppResult.Ok(defaultNotifSettings)
        viewModel.refresh()
        advanceUntilIdle()

        // When
        viewModel.onNewQuizzesToggle(false)
        advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.state.value.notificationSettings?.newQuizzes)
    }

    @Test
    fun `onNewQuizzesToggle reverts state and sends ShowError on failure`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Ok(defaultNotifSettings)
        coEvery { userRepository.updateNotificationSettings(any()) } returns AppResult.Err(AppError.NoInternet)
        viewModel.refresh()
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.onNewQuizzesToggle(false)
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is ProfileEvent.ShowError)
        }
        assertEquals(true, viewModel.state.value.notificationSettings?.newQuizzes)
    }

    @Test
    fun `onNewQuizzesToggle does nothing when notificationSettings is null`() = runTest {
        // Given
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()
        assertNull(viewModel.state.value.notificationSettings)

        // When
        viewModel.onNewQuizzesToggle(false)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.notificationSettings)
        coVerify(exactly = 0) { userRepository.updateNotificationSettings(any()) }
    }

    @Test
    fun `onQuizResultsToggle updates notificationSettings quizResults`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Ok(defaultNotifSettings)
        coEvery { userRepository.updateNotificationSettings(any()) } returns AppResult.Ok(defaultNotifSettings)
        viewModel.refresh()
        advanceUntilIdle()

        // When
        viewModel.onQuizResultsToggle(false)
        advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.state.value.notificationSettings?.quizResults)
    }

    @Test
    fun `onQuizResultsToggle reverts state and sends ShowError on failure`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Ok(defaultNotifSettings)
        coEvery { userRepository.updateNotificationSettings(any()) } returns AppResult.Err(AppError.ServerError)
        viewModel.refresh()
        advanceUntilIdle()

        // When
        viewModel.events.test {
            viewModel.onQuizResultsToggle(false)
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is ProfileEvent.ShowError)
        }
        assertEquals(true, viewModel.state.value.notificationSettings?.quizResults)
    }

    @Test
    fun `onGroupInvitesToggle updates notificationSettings groupInvites`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Ok(defaultNotifSettings)
        coEvery { userRepository.updateNotificationSettings(any()) } returns AppResult.Ok(defaultNotifSettings)
        viewModel.refresh()
        advanceUntilIdle()

        // When
        viewModel.onGroupInvitesToggle(true)
        advanceUntilIdle()

        // Then
        assertEquals(true, viewModel.state.value.notificationSettings?.groupInvites)
    }

    @Test
    fun `onDeadlineReminderChange updates notificationSettings deadlineReminder`() = runTest {
        // Given
        coEvery { userRepository.getUser() } returns AppResult.Ok(User(avatarUrl = null, firstName = "John", lastName = "Doe"))
        coEvery { userRepository.getNotificationSettings() } returns AppResult.Ok(defaultNotifSettings)
        coEvery { userRepository.updateNotificationSettings(any()) } returns AppResult.Ok(defaultNotifSettings)
        viewModel.refresh()
        advanceUntilIdle()

        // When
        viewModel.onDeadlineReminderChange("2days")
        advanceUntilIdle()

        // Then
        assertEquals("2days", viewModel.state.value.notificationSettings?.deadlineReminder)
    }

    @Test
    fun `onUpdateName updates state firstName and lastName on success`() = runTest {
        // Given
        coEvery { userRepository.updateName("Alice", "Wonder") } returns AppResult.Ok(Unit)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onUpdateName("Alice", "Wonder")
        advanceUntilIdle()

        // Then
        assertEquals("Alice", viewModel.state.value.firstName)
        assertEquals("Wonder", viewModel.state.value.lastName)
    }

    @Test
    fun `onUpdateName sends ShowError event on failure`() = runTest {
        // Given
        coEvery { userRepository.updateName(any(), any()) } returns AppResult.Err(AppError.NoInternet)

        // When
        viewModel.events.test {
            viewModel.onUpdateName("Alice", "Wonder")
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is ProfileEvent.ShowError)
        }
    }

    @Test
    fun `onUploadAvatar keeps avatarUrl present on success`() = runTest {
        // Given
        val uri = mockk<Uri>()
        coEvery { userRepository.uploadAvatar(uri) } returns AppResult.Ok(null)
        userFlow.value = User(avatarUrl = "http://example.com/avatar.png", firstName = "John", lastName = "Doe", email = "")
        viewModel = ProfileViewModel(userRepository, authRepository, personalRepository, sessionRepository)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onUploadAvatar(uri)
        advanceUntilIdle()

        // Then
        assertNotNull(viewModel.state.value.avatarUrl)
    }

    @Test
    fun `onUploadAvatar sends ShowError event on failure`() = runTest {
        // Given
        val uri = mockk<Uri>()
        coEvery { userRepository.uploadAvatar(uri) } returns AppResult.Err(AppError.ServerError)

        // When
        viewModel.events.test {
            viewModel.onUploadAvatar(uri)
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is ProfileEvent.ShowError)
        }
    }

    @Test
    fun `onDeleteAvatar sets avatarUrl to null on success`() = runTest {
        // Given
        coEvery { userRepository.deleteAvatar() } returns AppResult.Ok(Unit)
        userFlow.value = User(avatarUrl = "http://example.com/avatar.png", firstName = "John", lastName = "Doe", email = "")
        viewModel = ProfileViewModel(userRepository, authRepository, personalRepository, sessionRepository)
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        viewModel.onDeleteAvatar()
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.avatarUrl)
    }

    @Test
    fun `onDeleteAvatar sends ShowError event on failure`() = runTest {
        // Given
        coEvery { userRepository.deleteAvatar() } returns AppResult.Err(AppError.ServerError)

        // When
        viewModel.events.test {
            viewModel.onDeleteAvatar()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is ProfileEvent.ShowError)
        }
    }

    @Test
    fun `logout calls authRepository logout`() = runTest {
        // When
        viewModel.logout()
        advanceUntilIdle()

        // Then
        coVerify { authRepository.logout() }
    }

    @Test
    fun `deleteAccount calls sessionRepository clearSession on success`() = runTest {
        // Given
        coEvery { userRepository.deleteAccount() } returns AppResult.Ok(Unit)

        // When
        viewModel.deleteAccount()
        advanceUntilIdle()

        // Then
        coVerify { sessionRepository.clearSession() }
    }

    @Test
    fun `deleteAccount sends ShowError event on failure`() = runTest {
        // Given
        coEvery { userRepository.deleteAccount() } returns AppResult.Err(AppError.Forbidden)

        // When
        viewModel.events.test {
            viewModel.deleteAccount()
            advanceUntilIdle()

            // Then
            val event = awaitItem()
            assertTrue(event is ProfileEvent.ShowError)
        }
    }

    @Test
    fun `deleteAccount does not call clearSession on failure`() = runTest {
        // Given
        coEvery { userRepository.deleteAccount() } returns AppResult.Err(AppError.Forbidden)

        // When
        viewModel.deleteAccount()
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { sessionRepository.clearSession() }
    }

    @Test
    fun `userFlow changes update state in real time`() = runTest {
        // Given
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        userFlow.value = User(avatarUrl = null, firstName = "Jane", lastName = "Smith", email = "jane@example.com")
        advanceUntilIdle()

        // Then
        assertEquals("Jane", viewModel.state.value.firstName)
        assertEquals("Smith", viewModel.state.value.lastName)
        assertEquals("jane@example.com", viewModel.state.value.email)
    }

    @Test
    fun `themeModeFlow changes update themeMode in state`() = runTest {
        // Given
        backgroundScope.launch { viewModel.state.collect {} }
        advanceUntilIdle()

        // When
        themeModeFlow.value = ThemeMode.SYSTEM
        advanceUntilIdle()

        // Then
        assertEquals(ThemeMode.SYSTEM, viewModel.state.value.themeMode)
    }
}
