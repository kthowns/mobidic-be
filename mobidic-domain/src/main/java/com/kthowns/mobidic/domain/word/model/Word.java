package com.kthowns.mobidic.domain.word.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Word(
        UUID id,
        UUID vocabularyId,
        String expression,
        LocalDateTime createdAt
) {
    public static Word create(UUID vocabularyId, String expression) {
        return new Word(
                null,
                vocabularyId,
                expression,
                null
        );
    }

    public Word updateExpression(String expression) {
        return new Word(
                this.id,
                this.vocabularyId,
                expression,
                this.createdAt
        );
    }
}
