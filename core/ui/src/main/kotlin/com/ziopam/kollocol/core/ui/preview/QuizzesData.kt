package com.ziopam.kollocol.core.ui.preview

import com.ziopam.kollocol.core.common.TimeFormatter
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode
import com.ziopam.kollocol.domain.model.QuizParticipant
import com.ziopam.kollocol.domain.model.ReviewAnswer
import com.ziopam.kollocol.domain.model.ReviewQuestion

val quizzesInfoExample = listOf(
    QuizInfo(
        accessCode = "127287",
        totalTime = TimeFormatter.formatTime(8400),
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

val previewQuestionsOpen = listOf(
    ReviewQuestion("q1", "Какие типы данных (различные по хранению в памяти) используются в iOS? Чем отличается стек от кучи?", "open", null, "Value и Reference типы. Стек имеет неизменяемые размеры, а куча — случайная доступная область оперативной памяти.", 2, 0),
    ReviewQuestion("q2", "Что такое ARC?", "open", null, "Automatic Reference Counting — механизм управления памятью.", 3, 1),
    ReviewQuestion("q3", "Что такое optional в Swift?", "open", null, "Тип, который может содержать значение или nil.", 2, 2),
)

val previewAnswersOpen = listOf(
    ReviewAnswer("q1", "Value и Reference типы. Стек имеет неизменяемые размеры, а куча — случайная доступная область оперативной памяти. Стек быстрее кучи", false, false, 0),
    ReviewAnswer("q2", "", false, false, 0),
)

val previewQuestionsSingle = listOf(
    ReviewQuestion("q1", "Какой из перечисленных типов является Value type в Swift?", "single", listOf("class", "struct", "closure", "tuple"), "1", 2, 0),
    ReviewQuestion("q2", "Что возвращает функция без явного return?", "single", listOf("nil", "0", "Void", "false"), "2", 2, 1),
    ReviewQuestion("q3", "Какой модификатор доступа самый ограниченный?", "single", listOf("private", "fileprivate", "internal", "open"), "0", 2, 2),
)

val previewAnswersSingle = listOf(
    ReviewAnswer("q1", "1", true, true, 2),
    ReviewAnswer("q2", "0", false, true, 0),
)

val previewQuestionsMultiple = listOf(
    ReviewQuestion("q1", "Выберите все Value-типы из списка:", "multiple", listOf("Int", "String", "Array", "class MyClass", "struct MyStruct"), "[0,1,2,4]", 3, 0),
    ReviewQuestion("q2", "Какие утверждения про протоколы верны?", "multiple", listOf("Протокол может иметь реализацию по умолчанию", "Класс может реализовывать несколько протоколов", "Протокол — это тип"), "[0,1,2]", 3, 1),
)

val previewAnswersMultiple = listOf(
    ReviewAnswer("q1", "[0,1,4]", false, false, 0),
    ReviewAnswer("q2", "[0,1,2]", true, true, 3),
)

val quizParticipantReviewExample = listOf(
    QuizParticipant(
        userId = "1",
        firstName = "Арсений",
        lastName = "Потякин",
        email = "example@kollocol.app",
        avatarUrl = null,
        sessionStatus = "finished",
        reviewStatus = "reviewed",
        totalScore = 8,
        maxPossibleScore = 10
    ),
    QuizParticipant(
        userId = "2",
        firstName = "Иван",
        lastName = "Иванов",
        email = "ivan@kollocol.app",
        avatarUrl = null,
        sessionStatus = "finished",
        reviewStatus = "pending_review",
        totalScore = 0,
        maxPossibleScore = 10
    ),
    QuizParticipant(
        userId = "3",
        firstName = "Петя",
        lastName = "Петяков",
        email = "petya@kollocol.app",
        avatarUrl = null,
        sessionStatus = "in_progress",
        reviewStatus = "pending_review",
        totalScore = 0,
        maxPossibleScore = 10
    ),
    QuizParticipant(
        userId = "4",
        firstName = "Мария",
        lastName = "Петрова",
        email = "maria@kollocol.app",
        avatarUrl = null,
        sessionStatus = "joined",
        reviewStatus = "pending_review",
        totalScore = 0,
        maxPossibleScore = 10
    )
)
