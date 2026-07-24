package com.kthowns.mobidic.domain.global.model;

import java.time.Instant;

public record AuditTime(
        Instant createdAt,
        Instant updatedAt
) {
    public static AuditTime of(Instant createdAt, Instant updatedAt) {
        return new AuditTime(createdAt, updatedAt);
    }

    public static AuditTime create() {
        Instant now = Instant.now();
        return of(now, now);
    }

    public AuditTime update() {
        return of(this.createdAt, Instant.now());
    }

    public static AuditTime update(AuditTime auditTime) {
        if (auditTime == null) {
            return create();
        }
        return auditTime.update();
    }
}
