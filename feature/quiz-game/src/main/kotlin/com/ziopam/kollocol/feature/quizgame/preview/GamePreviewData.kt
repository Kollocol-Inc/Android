package com.ziopam.kollocol.feature.quizgame.preview

import com.ziopam.kollocol.domain.model.GameParticipant
import com.ziopam.kollocol.domain.model.GameQuestion
import com.ziopam.kollocol.domain.model.LeaderboardEntry
import com.ziopam.kollocol.domain.model.QuestionType

object GamePreviewData {
    val gameParticipants = listOf(
        GameParticipant("1", "Арсений Потякин", "example@kollocol.app", isCreator = true),
        GameParticipant("2", "Иван Иванов", "ivan@kollocol.app"),
        GameParticipant("3", "Мария Петрова", "maria@kollocol.app")
    )

    val leaderboardEntries = listOf(
        LeaderboardEntry(1, "user1", "Арсений Потякин", "example@kollocol.app", score = 3600),
        LeaderboardEntry(2, "user2", "Иван Иванов", "ivan@kollocol.app", score = 2400),
        LeaderboardEntry(3, "user3", "Мария Петрова", "maria@kollocol.app", score = 1200, isOnline = false),
        LeaderboardEntry(4, "user4", "Светлана Норимовна", "sveta@kollocol.app", score = 237),
        LeaderboardEntry(5, "user5", "Пётр Сидоров", "petr@kollocol.app", score = 0)
    )

    val question = GameQuestion(
        id = "1",
        text = "Какие типы данных (различные по хранению в памяти) используются в iOS? Чем отличается стек от кучи?",
        type = QuestionType.SINGLE,
        options = listOf("Очев", "Несложно", "Не уверен", "Помощь зала"),
        questionIndex = 0,
        totalQuestions = 5,
        maxScore = 1,
        timeLimitMs = 30000
    )
}