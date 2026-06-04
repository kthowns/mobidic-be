package com.kthowns.mobidic.domain.quiz.model;

import java.util.UUID;

public record QuizAnswer(
        UUID userId,
        UUID wordId,
        UUID quizId,
        String answer
) {
    public static QuizAnswer of(
            UUID userId,
            UUID wordId,
            UUID quizId,
            String answer) {
        return new QuizAnswer(userId, wordId, quizId, answer);
    }
}
