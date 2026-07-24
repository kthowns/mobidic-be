package com.kthowns.mobidic.domain.vocabulary.model;

import com.kthowns.mobidic.domain.global.model.AuditTime;

import java.util.UUID;

public record Vocabulary(
        UUID id,
        UUID userId,
        String title,
        String description,
        long wordCount,
        AuditTime auditTime
) {
    public static Vocabulary create(UUID userId, String title, String description, long wordCount) {
        return new Vocabulary(
                null,
                userId,
                title,
                description,
                wordCount,
                AuditTime.create()
        );
    }

    public Vocabulary update(String title, String description) {
        return new Vocabulary(
                this.id,
                this.userId,
                title != null ? title : this.title,
                description != null ? description : this.description,
                this.wordCount,
                AuditTime.update(this.auditTime)
        );
    }
}
