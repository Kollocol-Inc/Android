package com.ziopam.kollocol.domain.repository

import com.ziopam.kollocol.core.common.AppResult

data class VerifyResult(
    val isRegistered: Boolean
)

interface AuthRepository {
    suspend fun login(email: String): AppResult<Unit>
    suspend fun resendCode(email: String): AppResult<Unit>
    suspend fun verifyCode(email: String, code: String): AppResult<VerifyResult>
    suspend fun logout(): AppResult<Unit>
}