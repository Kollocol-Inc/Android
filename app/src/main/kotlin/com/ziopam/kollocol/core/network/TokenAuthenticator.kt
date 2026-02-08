package com.ziopam.kollocol.core.network

import com.google.gson.Gson
import com.ziopam.kollocol.data.datasource.remote.auth.AuthApi
import com.ziopam.kollocol.data.datasource.remote.auth.RefreshTokenRequestDto
import com.ziopam.kollocol.domain.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val gson: Gson,
    private val baseUrl: String
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request
        if (request.header("Authorization") == null) {
            return null
        }

        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = runBlocking { sessionRepository.getRefreshToken() }
            ?: return null

        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val authApi = retrofit.create(AuthApi::class.java)

        return try {
            val refreshResponse = runBlocking {
                authApi.refresh(RefreshTokenRequestDto(refreshToken))
            }

            val newAccess = refreshResponse.accessToken.orEmpty()
            val newRefresh = refreshResponse.refreshToken.orEmpty()

            if (newAccess.isBlank()) {
                return null
            }

            runBlocking {
                sessionRepository.saveTokens(newAccess, newRefresh)
            }

            request.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()
        } catch (e: HttpException) {
            val code = e.code()
            if (code == 401 || code == 403) {
                runBlocking { sessionRepository.clearSession() }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var current = response.priorResponse
        while (current != null) {
            count++
            current = current.priorResponse
        }
        return count
    }
}
