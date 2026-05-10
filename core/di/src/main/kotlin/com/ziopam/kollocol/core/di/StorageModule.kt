package com.ziopam.kollocol.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.ziopam.kollocol.core.session.datastore.dataStore
import com.ziopam.kollocol.data.storage.room.AppDatabase
import com.ziopam.kollocol.data.storage.room.GroupDao
import com.ziopam.kollocol.data.storage.room.GroupDetailDao
import com.ziopam.kollocol.data.storage.room.MIGRATION_1_2
import com.ziopam.kollocol.data.storage.room.MIGRATION_2_3
import com.ziopam.kollocol.data.storage.room.MIGRATION_3_4
import com.ziopam.kollocol.data.storage.room.MIGRATION_4_5
import com.ziopam.kollocol.data.storage.room.MIGRATION_5_6
import com.ziopam.kollocol.data.storage.room.QuizInstanceDao
import com.ziopam.kollocol.data.storage.room.TemplateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "kollocol_database"
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6).build()

    @Provides
    @Singleton
    fun provideQuizInstanceDao(database: AppDatabase): QuizInstanceDao =
        database.quizInstanceDao()
    
    @Provides
    @Singleton
    fun provideTemplateDao(database: AppDatabase): TemplateDao =
        database.templateDao()

    @Provides
    @Singleton
    fun provideGroupDao(database: AppDatabase): GroupDao =
        database.groupDao()

    @Provides
    @Singleton
    fun provideGroupDetailDao(database: AppDatabase): GroupDetailDao =
        database.groupDetailDao()
}