package com.kimtaeyang.mobidic.dictionary.dto;

import com.kimtaeyang.mobidic.dictionary.entity.Definition;
import com.kimtaeyang.mobidic.dictionary.type.PartOfSpeech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefinitionDto {
    private UUID id;
    private UUID wordId;
    private String definition;
    private PartOfSpeech part;

    public static DefinitionDto fromEntity (Definition definition) {
        return DefinitionDto.builder()
                .id(definition.getId())
                .definition(definition.getDefinition())
                .wordId(definition.getWord().getId())
                .part(definition.getPart())
                .build();
    }
}
