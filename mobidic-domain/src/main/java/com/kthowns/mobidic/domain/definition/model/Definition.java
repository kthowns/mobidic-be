package com.kthowns.mobidic.domain.definition.model;

import java.util.UUID;

public record Definition(
        UUID id,
        UUID wordId,
        String meaning,
        PartOfSpeech part
) {
    public static Definition create(UUID wordId, String meaning, PartOfSpeech part) {
        return new Definition(
                null,
                wordId,
                meaning,
                part
        );
    }

    public Definition update(String meaning, PartOfSpeech part) {
        return new Definition(
                this.id,
                this.wordId,
                meaning,
                part
        );
    }
}
