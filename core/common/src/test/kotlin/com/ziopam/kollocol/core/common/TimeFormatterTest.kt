package com.ziopam.kollocol.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {

    @Test
    fun `formatTime returns 0 с for zero seconds`() {
        // When
        val result = TimeFormatter.formatTime(0)

        // Then
        assertEquals("0 с", result)
    }

    @Test
    fun `formatTime clamps negative values to 0 с`() {
        // Given
        val negativeSeconds = -10

        // When
        val result = TimeFormatter.formatTime(negativeSeconds)

        // Then
        assertEquals("0 с", result)
    }

    @Test
    fun `formatTime returns seconds only for values under 60`() {
        // Given
        val seconds = 59

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("59 с", result)
    }

    @Test
    fun `formatTime returns minutes only when seconds are zero`() {
        // Given
        val seconds = 120

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("2 м", result)
    }

    @Test
    fun `formatTime returns minutes and seconds when both are nonzero`() {
        // Given
        val seconds = 90

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("1 м 30 с", result)
    }

    @Test
    fun `formatTime returns hours only when minutes and seconds are zero`() {
        // Given
        val seconds = 3600

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("1 ч", result)
    }

    @Test
    fun `formatTime returns hours and seconds when minutes are zero`() {
        // Given
        val seconds = 3601

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("1 ч 1 с", result)
    }

    @Test
    fun `formatTime returns hours and minutes when seconds are zero`() {
        // Given
        val seconds = 3660

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("1 ч 1 м", result)
    }

    @Test
    fun `formatTime returns hours minutes and seconds when all nonzero`() {
        // Given
        val seconds = 3661

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("1 ч 1 м 1 с", result)
    }

    @Test
    fun `formatTime returns correct value for multiple hours`() {
        // Given
        val seconds = 7200

        // When
        val result = TimeFormatter.formatTime(seconds)

        // Then
        assertEquals("2 ч", result)
    }

    @Test
    fun `formatTime returns 1 с for single second`() {
        // When
        val result = TimeFormatter.formatTime(1)

        // Then
        assertEquals("1 с", result)
    }

    @Test
    fun `formatTime returns 1 м for exactly 60 seconds`() {
        // When
        val result = TimeFormatter.formatTime(60)

        // Then
        assertEquals("1 м", result)
    }

    @Test
    fun `formatTime returns 1 м 1 с for 61 seconds`() {
        // When
        val result = TimeFormatter.formatTime(61)

        // Then
        assertEquals("1 м 1 с", result)
    }
}
