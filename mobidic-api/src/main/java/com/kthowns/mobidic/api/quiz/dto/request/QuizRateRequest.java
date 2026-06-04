package com.kthowns.mobidic.api.quiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizRateRequest {
    @NotBlank(message = "토큰은 필수 입력값 입니다.")
    private String token;
    @NotBlank(message = "답안은 필수 입력값 입니다.")
    private String answer;
}