package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.quiz.QuizApi
import com.ziopam.kollocol.data.storage.room.QuizInstanceDao
import com.ziopam.kollocol.data.storage.room.toQuizInfo
import com.ziopam.kollocol.domain.repository.QuizRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val api: QuizApi,
    private val safeApiCall: SafeApiCall,
    private val quizDao: QuizInstanceDao,
) : QuizRepository {

    override val participatingQuizzes = quizDao.getQuizzesByType("participating").map { entities ->
        entities.map { it.toQuizInfo() }
    }

    override val hostingQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.map { it.toQuizInfo() }
    }

    override val runningQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.filter {it.status != "pending_review" && it.status != "reviewed"}.map { it.toQuizInfo() }
    }

    override val pendingQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.filter { it.status == "pending_review" }
            .map { it.toQuizInfo() }
    }

    override val reviewedQuizzes = quizDao.getQuizzesByType("hosting").map { entities ->
        entities.filter { it.status == "reviewed" }
            .map { it.toQuizInfo() }
    }

    override suspend fun getParticipatingQuizzes(sessionStatus: String?): AppResult<Unit> {
        val result = safeApiCall.call {
            api.getParticipatingInstances(sessionStatus)
        }

        return when (result) {
            is AppResult.Ok -> {
                val entities = result.value.instances.map { participatingDto ->
                    participatingDto.instance.toEntity("participating")
                }
                quizDao.insertQuizzes(entities)

                AppResult.Ok(Unit)
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

    override suspend fun getHostingQuizzes(status: String?): AppResult<Unit> {
        val result = safeApiCall.call {
            api.getHostingInstances(status)
        }

        return when (result) {
            is AppResult.Ok -> {
                val entities = result.value.instances.map { it.toEntity("hosting") }
                quizDao.insertQuizzes(entities)

                AppResult.Ok(Unit)
            }
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }
}