package com.kthowns.mobidic.domain.term.model;

import com.kthowns.mobidic.domain.global.model.AuditTime;

public record Term(
        Long id,
        TermType type,
        String version,
        boolean required,
        String content,
        AuditTime auditTime
) {
    public static Term create(TermType type, String version, boolean required, String content) {
        return new Term(
                null,
                type,
                version,
                required,
                content,
                AuditTime.create()
        );
    }
}
