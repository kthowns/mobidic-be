package com.kthowns.mobidic.domain.preset.service;

import com.kthowns.mobidic.domain.preset.repository.PresetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class PresetAppender {
    private final PresetRepository presetRepository;

    public void copyAllPresetsToUser(UUID userId) {
        presetRepository.copyAllPresetsToUser(userId);
    }
}
