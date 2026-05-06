package com.ziopam.kollocol.domain.model

data class QuizParticipant(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val avatarUrl: String?,
    val sessionStatus: String,
    val reviewStatus: String,
    val totalScore: Int,
    val maxPossibleScore: Int
)

data class ParticipantAnswers(
    val instanceTitle: String,
    val questions: List<ReviewQuestion>,
    val answers: List<ReviewAnswer>
)

data class ReviewQuestion(
    val id: String,
    val text: String,
    val type: String,
    val options: List<String>?,
    val correctAnswer: String,
    val maxScore: Int,
    val orderIndex: Int
)

data class ReviewAnswer(
    val questionId: String,
    val answer: String,
    val isCorrect: Boolean,
    val isReviewed: Boolean,
    val score: Int
)

data class QuizInstanceDetails(
    val title: String,
    val status: String,
    val deadline: String?
)

