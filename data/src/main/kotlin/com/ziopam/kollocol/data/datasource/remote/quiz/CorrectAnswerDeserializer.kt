package com.ziopam.kollocol.data.datasource.remote.quiz

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class CorrectAnswerDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): String {
        return when {
            json.isJsonPrimitive -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isNumber -> primitive.asInt.toString()
                    primitive.isString -> primitive.asString
                    else -> json.toString()
                }
            }
            json.isJsonArray -> {
                json.asJsonArray.joinToString(",") { it.asInt.toString() }
            }
            else -> json.toString()
        }
    }
}