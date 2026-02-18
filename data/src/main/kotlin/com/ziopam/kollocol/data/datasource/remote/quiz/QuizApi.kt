package com.ziopam.kollocol.data.datasource.remote.quiz

import retrofit2.http.GET
import retrofit2.http.Query

interface QuizApi {
    @GET("quizzes/instances/participating")
    suspend fun getParticipatingInstances(
        @Query("session_status") sessionStatus: String? = null
    ): GetParticipatingInstancesResponseDto

    @GET("quizzes/instances/hosting")
    suspend fun getHostingInstances(
        @Query("status") status: String? = null
    ): GetHostingInstancesResponseDto
}