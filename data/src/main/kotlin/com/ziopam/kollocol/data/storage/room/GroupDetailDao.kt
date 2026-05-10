package com.ziopam.kollocol.data.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupDetailDao {
    @Query("SELECT * FROM group_details WHERE id = :id")
    suspend fun getById(id: String): GroupDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(detail: GroupDetailEntity)
}
