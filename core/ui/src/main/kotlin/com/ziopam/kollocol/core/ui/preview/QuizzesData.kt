package com.ziopam.kollocol.core.ui.preview

import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode

val quizzesInfoExample = listOf(
    QuizInfo(
        accessCode = "127287",
        totalTime = "11 мин",
        totalQuestions = 10,
        mode = QuizMode.ASYNC,
        title = "Коллоквиум Android",
        deadline = "01.01.2027"
    ),
    QuizInfo(
        accessCode = "127287",
        totalQuestions = 10,
        mode = QuizMode.SYNC,
        title = "Коллоквиум iOS",
    )
)