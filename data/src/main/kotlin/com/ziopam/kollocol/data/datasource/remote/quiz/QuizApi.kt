package com.ziopam.kollocol.data.datasource.remote.quiz

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    
    @GET("quizzes/templates")
    suspend fun getTemplates(): GetTemplatesResponseDto

    @POST("quizzes/templates")
    suspend fun createTemplate(@Body request: CreateTemplateRequestDto): CreateTemplateResponseDto

    @POST("quizzes/instances")
    suspend fun createInstance(@Body request: CreateInstanceRequestDto): CreateInstanceResponseDto
}