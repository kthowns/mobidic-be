package com.kthowns.mobidic.domain.word.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Word {
    private UUID id;
    private UUID vocabularyId;
    private String expression;
    private LocalDateTime createdAt;
}
