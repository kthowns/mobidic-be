package com.kthowns.mobidic.domain.auth.repository;

import java.util.UUID;

public interface BlackListRepository {
    void saveDeactivated(UUID userId, long ttlMillis);

    boolean existsDeactivated(UUID userId);
}
