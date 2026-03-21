package com.ziopam.kollocol.data.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [QuizInstanceEntity::class, TemplateEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizInstanceDao(): QuizInstanceDao
    abstract fun templateDao(): TemplateDao
}