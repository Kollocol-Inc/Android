package com.ziopam.kollocol.core.network.api

import com.ziopam.kollocol.core.network.model.RefreshTokenRequestDto
import com.ziopam.kollocol.core.network.model.RefreshTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequestDto): RefreshTokenResponseDto
}