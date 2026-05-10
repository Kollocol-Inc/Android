package com.ziopam.kollocol.data.storage.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ziopam.kollocol.data.datasource.remote.quiz.QuestionDTO
import com.ziopam.kollocol.data.datasource.remote.quiz.QuizSettingsDTO
import com.ziopam.kollocol.domain.model.GroupMember

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromQuizSettingsDTO(value: QuizSettingsDTO?): String? {
        return if (value == null) null else gson.toJson(value)
    }
    
    @TypeConverter
    fun toQuizSettingsDTO(value: String?): QuizSettingsDTO? {
        return if (value == null) null else gson.fromJson(value, QuizSettingsDTO::class.java)
    }
    
    @TypeConverter
    fun fromQuestionDTOList(value: List<QuestionDTO>?): String? {
        return if (value == null) null else gson.toJson(value)
    }
    
    @TypeConverter
    fun toQuestionDTOList(value: String?): List<QuestionDTO>? {
        if (value == null) return null
        val listType = object : TypeToken<List<QuestionDTO>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromGroupMemberList(value: List<GroupMember>): String = gson.toJson(value)

    @TypeConverter
    fun toGroupMemberList(value: String): List<GroupMember> {
        val listType = object : TypeToken<List<GroupMember>>() {}.type
        return gson.fromJson(value, listType)
    }
}