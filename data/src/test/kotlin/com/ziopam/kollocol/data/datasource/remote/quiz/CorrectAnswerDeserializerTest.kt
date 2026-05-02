package com.ziopam.kollocol.data.datasource.remote.quiz

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonPrimitive
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CorrectAnswerDeserializerTest {

    private lateinit var deserializer: CorrectAnswerDeserializer
    private val context: JsonDeserializationContext = mockk()

    @Before
    fun setup() {
        deserializer = CorrectAnswerDeserializer()
    }

    @Test
    fun `deserialize number primitive returns string representation`() {
        // Given
        val json = JsonPrimitive(2)

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals("2", result)
    }

    @Test
    fun `deserialize zero number returns zero string`() {
        // Given
        val json = JsonPrimitive(0)

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals("0", result)
    }

    @Test
    fun `deserialize string primitive returns the string value`() {
        // Given
        val json = JsonPrimitive("some text answer")

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals("some text answer", result)
    }

    @Test
    fun `deserialize empty string returns empty string`() {
        // Given
        val json = JsonPrimitive("")

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `deserialize array returns comma separated indices`() {
        // Given
        val json = JsonArray().apply {
            add(0)
            add(2)
        }

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals("0,2", result)
    }

    @Test
    fun `deserialize single element array returns single index string`() {
        // Given
        val json = JsonArray().apply { add(1) }

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals("1", result)
    }

    @Test
    fun `deserialize multi element array preserves order`() {
        // Given
        val json = JsonArray().apply {
            add(0)
            add(1)
            add(3)
        }

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals("0,1,3", result)
    }

    @Test
    fun `deserialize json object falls back to toString`() {
        // Given
        val json = com.google.gson.JsonObject().apply {
            addProperty("key", "value")
        }

        // When
        val result = deserializer.deserialize(json, String::class.java, context)

        // Then
        assertEquals(json.toString(), result)
    }
}
