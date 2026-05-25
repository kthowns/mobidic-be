package com.kthowns.mobidic.domain.definition.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Definition(
        UUID id,
        UUID wordId,
        String meaning,
        PartOfSpeech part
) {
}
