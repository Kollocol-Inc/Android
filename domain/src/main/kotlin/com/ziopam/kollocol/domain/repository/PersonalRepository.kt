package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.User

interface PersonalRepository {
    suspend fun getUser(): AppResult<User>
    suspend fun updateUser(user: User)
    suspend fun clear()
}
