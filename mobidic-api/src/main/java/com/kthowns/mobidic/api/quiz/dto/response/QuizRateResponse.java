package com.kthowns.mobidic.api.quiz.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizRateResponse {
    private Boolean isCorrect;
    private String correctAnswer;
}