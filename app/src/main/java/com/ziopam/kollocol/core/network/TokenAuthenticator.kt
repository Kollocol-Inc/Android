package com.ziopam.kollocol.core.network

import com.ziopam.kollocol.data.datasource.remote.auth.AuthApi
import com.ziopam.kollocol.data.datasource.remote.auth.RefreshTokenRequestDto
import com.ziopam.kollocol.domain.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val authApiNoAuth: AuthApi
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        return runBlocking {
            mutex.withLock {
                val currentAccess = sessionRepository.getAccessToken()
                val requestAccess = response.request.header("Authorization")
                    ?.removePrefix("Bearer")
                    ?.trim()

                if (!currentAccess.isNullOrBlank() && currentAccess != requestAccess) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccess")
                        .build()
                }

                val refresh = sessionRepository.getRefreshToken()
                if (refresh.isNullOrBlank()) {
                    sessionRepository.clearSession()
                    return@withLock null
                }

                try {
                    val refreshed = authApiNoAuth.refresh(RefreshTokenRequestDto(refresh))
                    val newAccess = refreshed.accessToken
                    val newRefresh = refreshed.refreshToken

                    if (newAccess.isNullOrBlank() || newRefresh.isNullOrBlank()) {
                        sessionRepository.clearSession()
                        return@withLock null
                    }

                    sessionRepository.saveTokens(newAccess, newRefresh)

                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccess")
                        .build()
                } catch (_: Exception) {
                    // TODO чекнуть логику разлогирования
                    sessionRepository.clearSession()
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var res: Response? = response
        var count = 1
        while (res?.priorResponse != null) {
            count++
            res = res.priorResponse
        }
        return count
    }
}
