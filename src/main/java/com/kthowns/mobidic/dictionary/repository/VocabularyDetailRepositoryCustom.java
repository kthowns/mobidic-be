package com.kthowns.mobidic.dictionary.repository;

import com.kthowns.mobidic.dictionary.dto.VocabularyDetail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VocabularyDetailRepositoryCustom {
    List<VocabularyDetail> findVocabularyDetails(UUID userId);

    Optional<VocabularyDetail> findVocabularyDetail(UUID vocabularyId, UUID userId);
}