package com.kthowns.mobidic.domain.word.model;

import com.kthowns.mobidic.domain.global.model.AuditTime;

import java.util.UUID;

public record Word(
        UUID id,
        UUID vocabularyId,
        String expression,
        AuditTime auditTime
) {
    public static Word create(UUID vocabularyId, String expression) {
        return new Word(
                null,
                vocabularyId,
                expression,
                AuditTime.create()
        );
    }

    public Word updateExpression(String expression) {
        return new Word(
                this.id,
                this.vocabularyId,
                expression,
                AuditTime.update(this.auditTime)
        );
    }
}
