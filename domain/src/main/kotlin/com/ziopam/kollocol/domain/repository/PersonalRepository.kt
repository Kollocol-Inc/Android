package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.domain.model.ThemeMode
import com.ziopam.kollocol.domain.model.User
import kotlinx.coroutines.flow.Flow

interface PersonalRepository {
    suspend fun getUser(): AppResult<User>
    fun getUserFlow(): Flow<User>
    suspend fun updateUser(user: User)
    suspend fun bumpAvatarVersion()
    suspend fun clear()
    fun getThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
