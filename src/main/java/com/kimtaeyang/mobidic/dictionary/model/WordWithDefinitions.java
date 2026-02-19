package com.kimtaeyang.mobidic.dictionary.model;

import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDto;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class WordWithDefinitions {
    private WordDto wordDto;
    private List<DefinitionDto> definitionDtos;
}
