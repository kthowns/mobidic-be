package com.kimtaeyang.mobidic.dto;

import com.kimtaeyang.mobidic.entity.Vocab;
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
public class VocabDto {
    private UUID id;
    private UUID memberId;
    private String title;
    private String description;
    private Timestamp createdAt;

    public static VocabDto fromEntity (Vocab vocab) {
        return VocabDto.builder()
                .title(vocab.getTitle())
                .memberId(vocab.getMember().getId())
                .id(vocab.getId())
                .description(vocab.getDescription())
                .createdAt(vocab.getCreatedAt())
                .build();
    }
}
