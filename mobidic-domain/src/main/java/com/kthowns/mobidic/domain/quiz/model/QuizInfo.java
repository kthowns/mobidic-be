package com.kthowns.mobidic.domain.quiz.model;

import lombok.Builder;

import java.util.List;

@Builder
public record QuizInfo(
        String token,
        String stem,
        List<String> options,
        long expMil
) {
}
