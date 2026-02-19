package com.kimtaeyang.mobidic.security.accesshandler;

import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordAccessHandler extends AccessHandler {
    private final WordRepository wordRepository;

    @Override
    boolean isResourceOwner(UUID resourceId) {
        return wordRepository.findById(resourceId)
                .map(Word::getVocabulary)
                .map(Vocabulary::getUser)
                .filter((m) -> getCurrentMemberId().equals(m.getId()))
                .isPresent();
    }
}
