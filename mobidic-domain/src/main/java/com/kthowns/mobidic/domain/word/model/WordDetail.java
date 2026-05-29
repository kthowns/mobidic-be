package com.kthowns.mobidic.domain.word.model;

import com.kthowns.mobidic.domain.definition.model.Definition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WordDetail(
        UUID id,
        String expression,
        double difficulty,
        double accuracy,
        boolean isLearned,
        List<Definition> definitions,
        LocalDateTime createdAt
) {
}
