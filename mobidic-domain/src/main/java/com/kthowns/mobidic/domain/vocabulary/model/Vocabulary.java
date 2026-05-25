package com.kthowns.mobidic.domain.vocabulary.model;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record Vocabulary(
        UUID id,
        UUID userId,
        String title,
        String description,
        long wordCount,
        LocalDateTime createdAt
) {
}
