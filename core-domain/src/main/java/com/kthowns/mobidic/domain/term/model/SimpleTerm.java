package com.kthowns.mobidic.domain.term.model;

import com.kthowns.mobidic.domain.global.model.AuditTime;

public record SimpleTerm(
        Long id,
        TermType type,
        String version,
        boolean required,
        String contentUri,
        AuditTime auditTime
) {
}
