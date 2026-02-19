package com.kimtaeyang.mobidic.dictionary.dto;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
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
public class VocabularyDto {
    private UUID id;
    private UUID memberId;
    private String title;
    private String description;
    private Timestamp createdAt;

    public static VocabularyDto fromEntity (Vocabulary vocabulary) {
        return VocabularyDto.builder()
                .title(vocabulary.getTitle())
                .memberId(vocabulary.getUser().getId())
                .id(vocabulary.getId())
                .description(vocabulary.getDescription())
                .createdAt(vocabulary.getCreatedAt())
                .build();
    }
}
