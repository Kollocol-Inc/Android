package com.ziopam.kollocol.data.repository

import com.ziopam.kollocol.core.common.AppResult
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.data.datasource.remote.auth.AuthApi
import com.ziopam.kollocol.data.datasource.remote.auth.LoginRequestDto
import com.ziopam.kollocol.data.datasource.remote.auth.VerifyCodeRequestDto
import com.ziopam.kollocol.domain.repository.AuthRepository
import com.ziopam.kollocol.domain.repository.SessionRepository
import com.ziopam.kollocol.domain.repository.VerifyResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val safeApiCall: SafeApiCall,
    private val sessionRepository: SessionRepository
) : AuthRepository {

    override suspend fun login(email: String): AppResult<Unit> =
        safeApiCall.call { api.login(LoginRequestDto(email)) }

    override suspend fun resendCode(email: String): AppResult<Unit> =
        safeApiCall.call { api.resendCode(LoginRequestDto(email)) }

    override suspend fun verifyCode(email: String, code: String): AppResult<VerifyResult> =
        safeApiCall.call {
            val res = api.verify(VerifyCodeRequestDto(code = code, email = email))

            val access = res.accessToken.orEmpty()
            val refresh = res.refreshToken.orEmpty()
            sessionRepository.saveTokens(access, refresh)

            // TODO Посмотреть, какие данные нужны в VerifyResult
            VerifyResult(
                isRegistered = (res.isRegistered == true),
                userId = res.userId
            )
        }

    override suspend fun logout(): AppResult<Unit> =
        safeApiCall.call {
            api.logout()
            sessionRepository.clearSession()
        }
}
