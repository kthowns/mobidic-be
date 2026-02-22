package com.kimtaeyang.mobidic.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizRateResponse {
    private Boolean isCorrect;
    private String correctAnswer;
}