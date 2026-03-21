package com.ziopam.kollocol.data.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizInstanceDao {
    @Query("SELECT * FROM quiz_instances WHERE instanceType = :type")
    fun getQuizzesByType(type: String): Flow<List<QuizInstanceEntity>>

    @Query("SELECT * FROM quiz_instances WHERE instanceType = :type AND status = :status")
    fun getQuizzesByTypeAndStatus(type: String, status: String): Flow<List<QuizInstanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<QuizInstanceEntity>)

    @Query("DELETE FROM quiz_instances WHERE instanceType = :type")
    suspend fun deleteQuizzesByType(type: String)

    @Query("DELETE FROM quiz_instances WHERE instanceType = :type AND status = :status")
    suspend fun deleteQuizzesByTypeAndStatus(type: String, status: String)

    @Query("DELETE FROM quiz_instances")
    suspend fun clearAll()
}