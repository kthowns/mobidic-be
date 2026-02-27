package com.kimtaeyang.mobidic.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizRateRequest {
    @NotBlank(message = "token is empty")
    private String token;
    @NotBlank(message = "answer is empty")
    private String answer;
}