package com.kthowns.mobidic.domain.term.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Term(
        Long id,
        TermType type,
        String version,
        boolean required,
        String content,
        LocalDateTime createdAt
) {
}

