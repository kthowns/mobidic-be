package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.dto.WordDetail;

import java.util.List;
import java.util.UUID;

public interface WordRepositoryCustom {
    List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean notLearned);
}
