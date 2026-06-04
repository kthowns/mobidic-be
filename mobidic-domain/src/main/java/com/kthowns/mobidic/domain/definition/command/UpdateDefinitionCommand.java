package com.kthowns.mobidic.domain.definition.command;

import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;

import java.util.UUID;

public record UpdateDefinitionCommand(
        UUID id,
        UUID wordId,
        String meaning,
        PartOfSpeech part
) {
    public static UpdateDefinitionCommand of(UUID definitionId, UUID wordId, String meaning, PartOfSpeech part) {
        return new UpdateDefinitionCommand(definitionId, wordId, meaning, part);
    }
}