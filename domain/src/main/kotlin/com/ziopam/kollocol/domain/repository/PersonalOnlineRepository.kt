package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.User
import kotlinx.coroutines.flow.Flow

interface PersonalOnlineRepository {
    val user: Flow<User>

    suspend fun getUser(): AppResult<Unit>
    suspend fun updateUser(user: User): AppResult<Unit>
}