package com.kimtaeyang.mobidic.model;

import com.kimtaeyang.mobidic.dto.DefDto;
import com.kimtaeyang.mobidic.dto.WordDto;
import lombok.*;

import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class WordWithDefs {
    private WordDto wordDto;
    private List<DefDto> defDtos;
}
