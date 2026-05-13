package com.kthowns.mobidic.api.dictionary.repository;

import com.kthowns.mobidic.api.dictionary.dto.WordDetail;

import java.util.List;
import java.util.UUID;

public interface WordRepositoryCustom {
    List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean notLearned);
}
