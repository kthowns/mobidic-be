package com.kthowns.mobidic.domain.preset.implementation;

import com.kthowns.mobidic.domain.preset.repository.PresetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PresetAppender {
    private final PresetRepository presetRepository;

    public void copyAllPresetsToUser(UUID userId) {
        presetRepository.copyAllPresetsToUser(userId);
    }
}
