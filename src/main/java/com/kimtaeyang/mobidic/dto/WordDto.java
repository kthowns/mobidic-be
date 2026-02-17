package com.kimtaeyang.mobidic.dto;

import com.kimtaeyang.mobidic.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordDto {
    private UUID id;
    private UUID vocabId;
    private String expression;
    private Timestamp createdAt;

    public static WordDto fromEntity (Word word) {
        return WordDto.builder()
                .id(word.getId())
                .vocabId(word.getVocab().getId())
                .expression(word.getExpression())
                .createdAt(word.getCreatedAt())
                .build();
    }
}
