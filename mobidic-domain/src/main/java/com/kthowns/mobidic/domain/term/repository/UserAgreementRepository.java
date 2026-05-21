package com.kthowns.mobidic.domain.term.repository;

import java.util.List;
import java.util.UUID;

public interface UserAgreementRepository {
    void saveAgreements(UUID userId, List<Long> termIds);
}
