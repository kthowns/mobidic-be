package com.kthowns.mobidic.domain.term.model;

import java.time.LocalDateTime;

public record SimpleTerm(
        Long id,
        TermType type,
        String version,
        boolean required,
        String contentUri,
        LocalDateTime createdAt
) {
}
