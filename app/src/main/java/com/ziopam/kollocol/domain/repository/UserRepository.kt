package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult

interface UserRepository {

    suspend fun registerUser(firstName: String, lastName: String): AppResult<Unit>
}