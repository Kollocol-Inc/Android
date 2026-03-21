package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.user.RegisterUserRequestDto
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.domain.model.User
import com.ziopam.kollocol.domain.repository.PersonalRepository
import com.ziopam.kollocol.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val safeApiCall: SafeApiCall,
    private val personalRepository: PersonalRepository
): UserRepository {
    override suspend fun registerUser(firstName: String, lastName: String): AppResult<Unit> {
        val result = safeApiCall.call {
            api.registerUser(RegisterUserRequestDto(firstName, lastName))
        }

        if (result is AppResult.Ok) {
            personalRepository.updateUser(
                User(
                    avatarUrl = result.value.avatarUrl,
                    firstName = result.value.firstName.orEmpty(),
                    lastName = result.value.lastName.orEmpty()
                )
            )
        }

        return when (result) {
            is AppResult.Ok -> AppResult.Ok(Unit)
            is AppResult.Err -> AppResult.Err(result.error)
        }
    }

}