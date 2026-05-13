package com.kthowns.mobidic.dictionary.dto;

import com.kthowns.mobidic.dictionary.entity.Definition;
import com.kthowns.mobidic.dictionary.type.PartOfSpeech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefinitionDto {
    private UUID id;
    private String meaning;
    private PartOfSpeech part;

    public static DefinitionDto fromEntity(Definition definition) {
        return DefinitionDto.builder()
                .id(definition.getId())
                .meaning(definition.getMeaning())
                .part(definition.getPart())
                .build();
    }
}
