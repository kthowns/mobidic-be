package com.kthowns.mobidic.domain.vocabulary.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Vocabulary(
        UUID id,
        UUID userId,
        String title,
        String description,
        long wordCount,
        LocalDateTime createdAt
) {
    public static Vocabulary create(UUID userId, String title, String description) {
        return new Vocabulary(
                null,
                userId,
                title,
                description,
                0,
                null
        );
    }

    public Vocabulary updateInfo(String title, String description) {
        return new Vocabulary(
                this.id,
                this.userId,
                title,
                description,
                this.wordCount,
                this.createdAt
        );
    }
}
