package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.user.UpdateUserRequestDto
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalOnlineRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// TODO избавиться от двух репозиториев
class PersonalOnlineRepositoryImpl @Inject constructor(
    val offlineRepository: PersonalRepository,
    val safeApiCall: SafeApiCall,
    val api: UserApi
) : PersonalOnlineRepository {
    override val user: Flow<User> = offlineRepository.user

    override suspend fun getUser(): AppResult<Unit>{
        val result = safeApiCall.call {
            api.getUser()
        }

        return when (result) {
            is AppResult.Ok -> {
                val user = result.value.toUser()
                offlineRepository.updateUser(user)
                AppResult.Ok(Unit)
            }
            is AppResult.Err -> result
        }
    }

    override suspend fun updateUser(user: User): AppResult<Unit> {
        val result = safeApiCall.call {
            api.updateUser(UpdateUserRequestDto(user.firstName, user.lastName))
        }

        return when (result) {
            is AppResult.Ok -> {
                offlineRepository.updateUser(user)
                AppResult.Ok(Unit)
            }
            is AppResult.Err -> result
        }
    }
}
