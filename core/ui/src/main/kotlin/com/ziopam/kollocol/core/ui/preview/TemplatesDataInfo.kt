package com.ziopam.kollocol.core.ui.preview

import com.ziopam.kollocol.core.common.TimeFormatter
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode

val templatesInfoExample = listOf(
    QuizInfo(
        accessCode = "",
        totalTime = TimeFormatter.formatTime(3600),
        totalQuestions = 10,
        mode = QuizMode.ASYNC,
        title = "Шаблон 1",
        deadline = null
    ),
    QuizInfo(
        accessCode = "",
        totalQuestions = 3,
        totalTime = TimeFormatter.formatTime(7200),
        mode = QuizMode.SYNC,
        title = "Шаблон 2",
    )
)