package com.ziopam.kollocol.data.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: TemplateEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<TemplateEntity>)
    
    @Query("SELECT * FROM templates ORDER BY id DESC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>
    
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: String): TemplateEntity?
    
    @Query("DELETE FROM templates WHERE id = :templateId")
    suspend fun deleteTemplate(templateId: String)
    
    @Query("DELETE FROM templates")
    suspend fun deleteAllTemplates()
}