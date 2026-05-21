package com.kthowns.mobidic.domain.preset.service;

import com.kthowns.mobidic.domain.preset.implementation.PresetAppender;
import com.kthowns.mobidic.domain.vocabulary.implementation.VocabularyReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresetVocabularyService {
    private final PresetAppender presetAppender;
    private final VocabularyReader vocabularyReader;

    @Transactional
    public void copyAllPresetToUser(UUID userId) {
        if (vocabularyReader.existsByUser(userId)) {
            return;
        }
        
        presetAppender.copyAllPresetsToUser(userId);
    }
}
