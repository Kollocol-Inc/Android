package com.ziopam.kollocol.data.datasource.remote.quiz

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @GET("quizzes/templates/{id}")
    suspend fun getTemplate(@Path("id") id: String): TemplateDTO

    @PUT("quizzes/templates/{id}")
    suspend fun updateTemplate(@Path("id") id: String, @Body request: CreateTemplateRequestDto)

    @DELETE("quizzes/templates/{id}")
    suspend fun deleteTemplate(@Path("id") id: String)

    @POST("ml/generate/template")
    suspend fun generateTemplate(@Body request: GenerateTemplateRequestDto): GeneratedTemplateResponseDto

    @POST("ml/generate/template/questions")
    suspend fun generateQuestions(@Body request: GenerateQuestionsRequestDto): GenerateQuestionsResponseDto
}