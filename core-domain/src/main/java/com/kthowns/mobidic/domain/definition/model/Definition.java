package com.kthowns.mobidic.domain.definition.model;

import com.kthowns.mobidic.domain.global.model.AuditTime;

import java.time.Instant;
import java.util.UUID;

public record Definition(
        UUID id,
        UUID wordId,
        String meaning,
        PartOfSpeech part,
        AuditTime auditTime
) {
    public static Definition of(UUID id, UUID wordId, String meaning, PartOfSpeech part, Instant createdAt, Instant updatedAt) {
        return new Definition(id, wordId, meaning, part, AuditTime.of(createdAt, updatedAt));
    }

    public static Definition create(UUID wordId, String meaning, PartOfSpeech part) {
        return new Definition(
                null,
                wordId,
                meaning,
                part,
                AuditTime.create()
        );
    }

    public Definition update(String meaning, PartOfSpeech part) {
        return new Definition(
                this.id,
                this.wordId,
                meaning,
                part,
                AuditTime.update(this.auditTime)
        );
    }
}
