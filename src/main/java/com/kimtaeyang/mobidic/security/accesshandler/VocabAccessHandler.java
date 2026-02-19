package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VocabAccessHandler extends AccessHandler {
    private final VocabularyRepository vocabularyRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return vocabularyRepository.findById(resourceId)
                .map(Vocabulary::getUser)
                .filter((m) -> getCurrentMemberId().equals(m.getId()))
                .isPresent();
    }
}
