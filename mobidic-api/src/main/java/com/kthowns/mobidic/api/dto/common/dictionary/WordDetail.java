package com.kthowns.mobidic.api.dto.common.dictionary;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record WordDetail(
        UUID id,
        String expression,
        double difficulty,
        double accuracy,
        boolean isLearned,
        List<DefinitionDto> definitions,
        LocalDateTime createdAt
) {
}
