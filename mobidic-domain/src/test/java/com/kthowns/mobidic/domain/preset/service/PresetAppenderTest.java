package com.kthowns.mobidic.domain.preset.service;

import com.kthowns.mobidic.domain.preset.repository.PresetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PresetAppenderTest {

    @Mock
    private PresetRepository presetRepository;

    @InjectMocks
    private PresetAppender presetAppender;

    @Test
    @DisplayName("copyAllPresetsToUser 테스트 - 사용자에게 프리셋 복사")
    void copyAllPresetsToUserTest() {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        presetAppender.copyAllPresetsToUser(userId);

        // Then
        verify(presetRepository).copyAllPresetsToUser(userId);
    }
}
