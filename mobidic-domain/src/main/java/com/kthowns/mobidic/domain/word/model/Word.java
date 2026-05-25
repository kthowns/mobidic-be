package com.kthowns.mobidic.domain.word.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Word(
        UUID id,
        UUID vocabularyId,
        String expression,
        LocalDateTime createdAt
) {
}
