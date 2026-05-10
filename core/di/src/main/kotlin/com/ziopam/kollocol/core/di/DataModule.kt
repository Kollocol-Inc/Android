@file:Suppress("unused")

package com.ziopam.kollocol.core.di

import com.ziopam.kollocol.data.repository.AuthRepositoryImpl
import com.ziopam.kollocol.data.repository.GameRepositoryImpl
import com.ziopam.kollocol.data.repository.GroupRepositoryImpl
import com.ziopam.kollocol.data.repository.NotificationRepositoryImpl
import com.ziopam.kollocol.data.repository.PersonalRepositoryImpl
import com.ziopam.kollocol.data.repository.QuizRepositoryImpl
import com.ziopam.kollocol.data.repository.UserRepositoryImpl
import com.ziopam.kollocol.domain.repository.AuthRepository
import com.ziopam.kollocol.domain.repository.GameRepository
import com.ziopam.kollocol.domain.repository.GroupRepository
import com.ziopam.kollocol.domain.repository.NotificationRepository
import com.ziopam.kollocol.domain.repository.PersonalRepository
import com.ziopam.kollocol.domain.repository.QuizRepository
import com.ziopam.kollocol.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindPersonalRepository(impl: PersonalRepositoryImpl): PersonalRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository
}
