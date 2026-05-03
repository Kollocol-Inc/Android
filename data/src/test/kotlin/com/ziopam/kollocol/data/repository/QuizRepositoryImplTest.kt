package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppError
import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.quiz.CreateInstanceResponseDto
import com.ziopam.kollocol.data.datasource.remote.quiz.CreateTemplateResponseDto
import com.ziopam.kollocol.data.datasource.remote.quiz.GetHostingInstancesResponseDto
import com.ziopam.kollocol.data.datasource.remote.quiz.GetParticipatingInstancesResponseDto
import com.ziopam.kollocol.data.datasource.remote.quiz.GetTemplatesResponseDto
import com.ziopam.kollocol.data.datasource.remote.quiz.InstanceDto
import com.ziopam.kollocol.data.datasource.remote.quiz.ParticipatingInstanceDto
import com.ziopam.kollocol.data.datasource.remote.quiz.QuizApi
import com.ziopam.kollocol.data.storage.room.QuizInstanceDao
import com.ziopam.kollocol.data.storage.room.QuizInstanceEntity
import com.ziopam.kollocol.data.storage.room.TemplateDao
import com.ziopam.kollocol.data.storage.room.TemplateEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuizRepositoryImplTest {

    private lateinit var api: QuizApi
    private lateinit var safeApiCall: SafeApiCall
    private lateinit var quizDao: QuizInstanceDao
    private lateinit var templateDao: TemplateDao
    private lateinit var repository: QuizRepositoryImpl

    private val hostingFlow = MutableStateFlow<List<QuizInstanceEntity>>(emptyList())
    private val participatingFlow = MutableStateFlow<List<QuizInstanceEntity>>(emptyList())
    private val templatesFlow = MutableStateFlow(emptyList<TemplateEntity>())

    private fun makeInstanceDto(
        id: String = "inst1",
        title: String = "Quiz",
        status: String = "active"
    ) = InstanceDto(
        id = id, title = title, accessCode = "CODE$id",
        quizType = "sync", status = status,
        totalQuestions = 5, createdAt = "2024-01-01"
    )

    @Before
    fun setup() {
        api = mockk(relaxed = true)
        safeApiCall = mockk(relaxed = true)
        quizDao = mockk(relaxed = true)
        templateDao = mockk(relaxed = true)

        every { quizDao.getQuizzesByType("hosting") } returns hostingFlow
        every { quizDao.getQuizzesByType("participating") } returns participatingFlow
        every { templateDao.getAllTemplates() } returns templatesFlow

        repository = QuizRepositoryImpl(api, safeApiCall, quizDao, templateDao)
    }

    @Test
    fun `getParticipatingQuizzes inserts entities into DAO on success`() = runTest {
        // Given
        val instanceDto = makeInstanceDto()
        val participatingDto = ParticipatingInstanceDto(instance = instanceDto, sessionStatus = "active")
        val responseDto = GetParticipatingInstancesResponseDto(instances = listOf(participatingDto))

        coEvery { safeApiCall.call<GetParticipatingInstancesResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        repository.getParticipatingQuizzes()

        // Then
        coVerify { quizDao.syncQuizzesByType("participating", any()) }
    }

    @Test
    fun `getParticipatingQuizzes returns Ok on success`() = runTest {
        // Given
        val responseDto = GetParticipatingInstancesResponseDto(instances = emptyList())
        coEvery { safeApiCall.call<GetParticipatingInstancesResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        val result = repository.getParticipatingQuizzes()

        // Then
        assertTrue(result is AppResult.Ok)
    }

    @Test
    fun `getParticipatingQuizzes returns Err on API failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetParticipatingInstancesResponseDto>(any()) } returns
            AppResult.Err(AppError.NoInternet)

        // When
        val result = repository.getParticipatingQuizzes()

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.NoInternet)
    }

    @Test
    fun `getParticipatingQuizzes does not insert into DAO on failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetParticipatingInstancesResponseDto>(any()) } returns
            AppResult.Err(AppError.Timeout)

        // When
        repository.getParticipatingQuizzes()

        // Then
        coVerify(exactly = 0) { quizDao.syncQuizzesByType(any(), any()) }
    }

    @Test
    fun `getHostingQuizzes inserts entities into DAO on success`() = runTest {
        // Given
        val responseDto = GetHostingInstancesResponseDto(instances = listOf(makeInstanceDto()))
        coEvery { safeApiCall.call<GetHostingInstancesResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        repository.getHostingQuizzes()

        // Then
        coVerify { quizDao.syncQuizzesByType("hosting", any()) }
    }

    @Test
    fun `getHostingQuizzes returns Ok on success`() = runTest {
        // Given
        val responseDto = GetHostingInstancesResponseDto(instances = emptyList())
        coEvery { safeApiCall.call<GetHostingInstancesResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        val result = repository.getHostingQuizzes()

        // Then
        assertTrue(result is AppResult.Ok)
    }

    @Test
    fun `getHostingQuizzes returns Err on API failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetHostingInstancesResponseDto>(any()) } returns
            AppResult.Err(AppError.ServerError)

        // When
        val result = repository.getHostingQuizzes()

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.ServerError)
    }

    @Test
    fun `getTemplates inserts templates into DAO on success`() = runTest {
        // Given
        val responseDto = GetTemplatesResponseDto(templates = emptyList())
        coEvery { safeApiCall.call<GetTemplatesResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        repository.getTemplates()

        // Then
        coVerify { templateDao.syncAllTemplates(any()) }
    }

    @Test
    fun `getTemplates returns Ok on success`() = runTest {
        // Given
        val responseDto = GetTemplatesResponseDto(templates = emptyList())
        coEvery { safeApiCall.call<GetTemplatesResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        val result = repository.getTemplates()

        // Then
        assertTrue(result is AppResult.Ok)
    }

    @Test
    fun `getTemplates returns Err on API failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<GetTemplatesResponseDto>(any()) } returns
            AppResult.Err(AppError.Forbidden)

        // When
        val result = repository.getTemplates()

        // Then
        assertTrue(result is AppResult.Err)
    }

    @Test
    fun `createTemplate returns Ok with templateId on success`() = runTest {
        // Given
        val responseDto = CreateTemplateResponseDto(templateId = "template-123")
        coEvery { safeApiCall.call<CreateTemplateResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        val result = repository.createTemplate(
            title = "Test",
            quizType = "async",
            questions = listOf(mapOf(
                "text" to "Q1", "type" to "single", "correct_answer" to 0,
                "max_score" to 10, "options" to listOf("A", "B"), "time_limit_sec" to 30
            ))
        )

        // Then
        assertTrue(result is AppResult.Ok)
        assertEquals("template-123", (result as AppResult.Ok).value)
    }

    @Test
    fun `createTemplate returns Err on API failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<CreateTemplateResponseDto>(any()) } returns
            AppResult.Err(AppError.BadRequest("Invalid data"))

        // When
        val result = repository.createTemplate(
            title = "Test",
            quizType = "async",
            questions = listOf(mapOf(
                "text" to "Q1", "type" to "single", "correct_answer" to 0,
                "max_score" to 10, "options" to null, "time_limit_sec" to 30
            ))
        )

        // Then
        assertTrue(result is AppResult.Err)
    }

    @Test
    fun `createInstance returns Ok on success`() = runTest {
        // Given
        val responseDto = CreateInstanceResponseDto(instanceId = "inst-1", accessCode = "CODE1")
        coEvery { safeApiCall.call<CreateInstanceResponseDto>(any()) } returns
            AppResult.Ok(responseDto)

        // When
        val result = repository.createInstance(
            templateId = "template-1",
            title = "Instance Title"
        )

        // Then
        assertTrue(result is AppResult.Ok)
    }

    @Test
    fun `createInstance returns Err on API failure`() = runTest {
        // Given
        coEvery { safeApiCall.call<CreateInstanceResponseDto>(any()) } returns
            AppResult.Err(AppError.NotFound)

        // When
        val result = repository.createInstance(
            templateId = "nonexistent",
            title = "Quiz"
        )

        // Then
        assertTrue(result is AppResult.Err)
        assertTrue((result as AppResult.Err).error is AppError.NotFound)
    }

    @Test
    fun `runningQuizzes filters out pending_review and reviewed statuses`() = runTest {
        // Given
        hostingFlow.value = listOf(
            makeEntity("1", "active"),
            makeEntity("2", "pending_review"),
            makeEntity("3", "reviewed"),
            makeEntity("4", "running")
        )

        // When
        val running = repository.runningQuizzes.first()

        // Then
        assertEquals(2, running.size)
        assertTrue(running.all { it.accessCode != "CODE2" && it.accessCode != "CODE3" })
    }

    @Test
    fun `pendingQuizzes returns only pending_review status`() = runTest {
        // Given
        hostingFlow.value = listOf(
            makeEntity("1", "active"),
            makeEntity("2", "pending_review"),
            makeEntity("3", "pending_review")
        )

        // When
        val pending = repository.pendingQuizzes.first()

        // Then
        assertEquals(2, pending.size)
    }

    @Test
    fun `reviewedQuizzes returns only reviewed status`() = runTest {
        // Given
        hostingFlow.value = listOf(
            makeEntity("1", "active"),
            makeEntity("2", "reviewed"),
            makeEntity("3", "pending_review")
        )

        // When
        val reviewed = repository.reviewedQuizzes.first()

        // Then
        assertEquals(1, reviewed.size)
    }

    private fun makeEntity(id: String, status: String) = QuizInstanceEntity(
        id = id, title = "Quiz $id", accessCode = "CODE$id",
        quizType = "sync", totalQuestions = 5, totalTime = null,
        deadline = null, instanceType = "hosting", status = status
    )
}
