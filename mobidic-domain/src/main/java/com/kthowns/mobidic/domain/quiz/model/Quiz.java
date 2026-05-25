package com.kthowns.mobidic.domain.quiz.model;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record Quiz(
        UUID id,
        UUID userId,
        UUID wordId,
        String stem,
        List<String> options,
        String answer
) {
}
