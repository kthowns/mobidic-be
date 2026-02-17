package com.kimtaeyang.mobidic.dto;

import com.kimtaeyang.mobidic.entity.Def;
import com.kimtaeyang.mobidic.type.PartOfSpeech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefDto {
    private UUID id;
    private UUID wordId;
    private String definition;
    private PartOfSpeech part;

    public static DefDto fromEntity (Def def) {
        return DefDto.builder()
                .id(def.getId())
                .definition(def.getDefinition())
                .wordId(def.getWord().getId())
                .part(def.getPart())
                .build();
    }
}
