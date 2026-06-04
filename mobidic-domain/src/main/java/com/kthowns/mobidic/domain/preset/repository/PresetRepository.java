package com.kthowns.mobidic.domain.preset.repository;

import java.util.UUID;

public interface PresetRepository {
    void copyAllPresetsToUser(UUID userId);
}
