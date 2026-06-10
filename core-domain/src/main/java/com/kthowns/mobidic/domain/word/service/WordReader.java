package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class WordReader {
    private final WordRepository wordRepository;

    public List<WordDetail> readDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean onlyNotLearned) {
        return wordRepository.readDetailsByVocabularyId(userId, vocabularyId, onlyNotLearned);
    }

    public Word readByIdAndUserId(UUID wordId, UUID userId) {
        return wordRepository.readByIdAndUserId(wordId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
    }
}
