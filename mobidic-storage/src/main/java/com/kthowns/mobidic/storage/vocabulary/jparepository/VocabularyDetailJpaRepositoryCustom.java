package com.kthowns.mobidic.storage.vocabulary.jparepository;


import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VocabularyDetailJpaRepositoryCustom {
    List<VocabularyDetail> findVocabularyDetails(UUID userId);

    Optional<VocabularyDetail> findVocabularyDetail(UUID vocabularyId, UUID userId);
}