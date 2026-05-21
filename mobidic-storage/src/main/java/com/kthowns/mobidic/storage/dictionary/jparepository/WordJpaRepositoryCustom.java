package com.kthowns.mobidic.storage.dictionary.jparepository;

import com.kthowns.mobidic.domain.dictionary.model.WordDetail;

import java.util.List;
import java.util.UUID;

public interface WordJpaRepositoryCustom {
    List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean notLearned);
}
