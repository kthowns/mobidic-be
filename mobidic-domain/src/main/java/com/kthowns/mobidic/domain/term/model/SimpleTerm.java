package com.kthowns.mobidic.domain.term.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SimpleTerm(
        Long id,
        TermType type,
        String version,
        boolean required,
        String contentUri,
        LocalDateTime createdAt
) {
}
