package com.kimtaeyang.mobidic.dictionary.dto;

import com.kimtaeyang.mobidic.dictionary.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordDto {
    private UUID id;
    private UUID vocabularyId;
    private String expression;
    private LocalDateTime createdAt;

    public static WordDto fromEntity(Word word) {
        return WordDto.builder()
                .id(word.getId())
                .vocabularyId(word.getVocabulary().getId())
                .expression(word.getExpression())
                .createdAt(word.getCreatedAt())
                .build();
    }
}
