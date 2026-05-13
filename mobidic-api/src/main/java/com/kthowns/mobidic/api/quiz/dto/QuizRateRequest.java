package com.kthowns.mobidic.api.quiz.dto;

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
    @NotBlank(message = "토큰은 필수 입력값 입니다.")
    private String token;
    @NotBlank(message = "답안은 필수 입력값 입니다.")
    private String answer;
}