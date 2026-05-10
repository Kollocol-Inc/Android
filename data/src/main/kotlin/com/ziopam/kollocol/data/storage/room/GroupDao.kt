package com.ziopam.kollocol.data.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups WHERE membershipType = :type")
    fun getGroupsByType(type: String): Flow<List<GroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<GroupEntity>)

    @Query("DELETE FROM groups WHERE membershipType = :type")
    suspend fun deleteGroupsByType(type: String)

    @Query("DELETE FROM groups")
    suspend fun clearAll()

    @Transaction
    suspend fun syncGroupsByType(type: String, groups: List<GroupEntity>) {
        deleteGroupsByType(type)
        insertGroups(groups)
    }
}
