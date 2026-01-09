package com.ziopam.kollocol.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.core.network.TokenAuthenticator
import com.ziopam.kollocol.core.network.AuthInterceptor
import com.ziopam.kollocol.data.datasource.remote.auth.AuthApi
import com.ziopam.kollocol.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://130.193.58.223:8080/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideSafeApiCall(gson: Gson): SafeApiCall = SafeApiCall(gson)

    // TODO Сделать логи только в debug
    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    @Named("no_authenticator")
    fun provideOkHttpNoAuthenticator(
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    @Provides
    @Singleton
    @Named("retrofit_no_authenticator")
    fun provideRetrofitNoAuthenticator(
        gson: Gson,
        @Named("no_authenticator") okHttp: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    @Named("authApiNoAuth")
    fun provideAuthApiNoAuth(
        @Named("retrofit_no_authenticator") retrofit: Retrofit
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        sessionRepository: SessionRepository,
        @Named("authApiNoAuth") authApiNoAuth: AuthApi
    ): TokenAuthenticator = TokenAuthenticator(sessionRepository, authApiNoAuth)


    @Provides @Singleton
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
}
