package com.kthowns.mobidic.domain.term.model;

import java.time.LocalDateTime;

public record Term(
        Long id,
        TermType type,
        String version,
        boolean required,
        String content,
        LocalDateTime createdAt
) {
    public static Term create(TermType type, String version, boolean required, String content) {
        return new Term(
                null,
                type,
                version,
                required,
                content,
                null
        );
    }
}
