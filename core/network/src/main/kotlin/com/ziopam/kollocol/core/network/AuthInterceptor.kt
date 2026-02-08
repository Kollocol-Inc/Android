package com.ziopam.kollocol.core.network

import com.ziopam.kollocol.domain.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionRepository: SessionRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()

        val token = runBlocking { sessionRepository.getAccessToken() }
        if (token.isNullOrBlank()) return chain.proceed(req)

        return chain.proceed(
            req.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )
    }
}
