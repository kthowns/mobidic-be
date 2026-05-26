package com.kthowns.mobidic.domain.vocabulary.repository;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VocabularyRepository {

    List<VocabularyDetail> readDetailsByUserId(UUID userId);

    Vocabulary append(Vocabulary vocabulary);

    Optional<Vocabulary> readByIdAndUserId(UUID vocabularyId, UUID userId);

    Optional<VocabularyDetail> readDetailById(UUID vocabularyId, UUID userId);

    boolean existsByIdAndUser_Id(UUID vocabularyId, UUID userId);

    void increaseWordCount(UUID vocabularyId);

    void decreaseWordCount(UUID vocabularyId);

    void delete(UUID vocabularyId, UUID userId);

    void update(Vocabulary vocabulary);

    boolean existsByTitleAndUserId(String title, UUID userId);

    boolean existsByTitleAndIdNotAndUserId(String title, UUID vocabularyId, UUID userId);

    boolean existsByUserId(UUID userId);
}
