package com.kthowns.mobidic.domain.term.repository;

import java.util.List;
import java.util.UUID;

public interface UserAgreementRepository {
    void appendAgreements(UUID userId, List<Long> termIds);
}
