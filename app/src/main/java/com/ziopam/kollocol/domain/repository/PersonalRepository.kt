package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.domain.model.User
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    val user: Flow<User>
    suspend fun updateUser(user: User)
    suspend fun clear()
}
