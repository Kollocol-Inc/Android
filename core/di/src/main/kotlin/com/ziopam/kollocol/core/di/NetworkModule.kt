package com.ziopam.kollocol.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ziopam.kollocol.core.network.AuthInterceptor
import com.ziopam.kollocol.core.network.SafeApiCall
import com.ziopam.kollocol.core.network.TokenAuthenticator
import com.ziopam.kollocol.data.datasource.remote.auth.AuthApi
import com.ziopam.kollocol.data.datasource.remote.group.GroupApi
import com.ziopam.kollocol.data.datasource.remote.notification.NotificationApi
import com.ziopam.kollocol.data.datasource.remote.quiz.QuizApi
import com.ziopam.kollocol.data.datasource.remote.user.UserApi
import com.ziopam.kollocol.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        sessionRepository: SessionRepository,
        gson: Gson
    ): TokenAuthenticator = TokenAuthenticator(
        sessionRepository = sessionRepository,
        gson = gson,
        baseUrl = BASE_URL
    )

    @Provides
    @Singleton
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .authenticator(tokenAuthenticator)
        .pingInterval(20, TimeUnit.SECONDS)
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

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideQuizApi(retrofit: Retrofit): QuizApi =
        retrofit.create(QuizApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi =
        retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun provideGroupApi(retrofit: Retrofit): GroupApi =
        retrofit.create(GroupApi::class.java)
}
