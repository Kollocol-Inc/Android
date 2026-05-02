package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.QuizInfo
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    val participatingQuizzes: Flow<List<QuizInfo>>
    val hostingQuizzes: Flow<List<QuizInfo>>
    val runningQuizzes: Flow<List<QuizInfo>>
    val pendingQuizzes: Flow<List<QuizInfo>>
    val reviewedQuizzes: Flow<List<QuizInfo>>
    val templates: Flow<List<QuizInfo>>

    suspend fun getParticipatingQuizzes(sessionStatus: String? = null): AppResult<Unit>
    suspend fun getHostingQuizzes(status: String? = null): AppResult<Unit>
    suspend fun getTemplates(): AppResult<Unit>
    suspend fun createTemplate(
        title: String,
        quizType: String,
        questions: List<Map<String, Any?>>,
        description: String? = null,
        randomOrder: Boolean = false
    ): AppResult<String>

    suspend fun createInstance(
        templateId: String,
        title: String,
        deadline: String? = null
    ): AppResult<Unit>
}