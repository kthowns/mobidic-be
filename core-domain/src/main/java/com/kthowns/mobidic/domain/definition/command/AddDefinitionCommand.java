package com.kthowns.mobidic.domain.definition.command;

import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;

public record AddDefinitionCommand(
        String meaning,
        PartOfSpeech part
) {
    public static AddDefinitionCommand of(String meaning, PartOfSpeech part) {
        return new AddDefinitionCommand(meaning, part);
    }
}