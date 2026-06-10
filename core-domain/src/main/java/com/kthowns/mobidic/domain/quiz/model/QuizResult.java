package com.kthowns.mobidic.domain.quiz.model;

import lombok.Builder;

@Builder
public record QuizResult(
        boolean isCorrect,
        String correctAnswer
) {
}
