package com.kthowns.mobidic.domain.quiz.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizResult {
    private boolean isCorrect;
    private String correctAnswer;
}
