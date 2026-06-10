package com.kthowns.mobidic.storage.word.jparepository;

import com.kthowns.mobidic.domain.word.model.WordDetail;

import java.util.List;
import java.util.UUID;

public interface WordJpaRepositoryCustom {
    List<WordDetail> findWordDetailsByVocabularyId(UUID userId, UUID vocabularyId, boolean notLearned);
}
