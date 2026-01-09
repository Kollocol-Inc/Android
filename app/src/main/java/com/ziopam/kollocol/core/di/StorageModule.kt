package com.ziopam.kollocol.core.di

import com.ziopam.kollocol.core.session.SessionDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideSessionDataStore(store: SessionDataStore): SessionDataStore = store
}
