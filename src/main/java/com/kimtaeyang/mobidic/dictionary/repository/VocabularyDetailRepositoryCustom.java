package com.kimtaeyang.mobidic.dictionary.repository;

import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDetail;

import java.util.List;
import java.util.UUID;

public interface VocabularyDetailRepositoryCustom {
    List<VocabularyDetail> getVocabularyDetails(UUID userId);
}