package com.kthowns.mobidic.domain.vocabulary.repository;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VocabularyRepository {

    List<VocabularyDetail> readDetailsByUserId(UUID userId);

    void append(Vocabulary vocabulary);

    void save(Vocabulary vocabulary);

    Optional<VocabularyDetail> readDetailById(UUID vocabularyId, UUID userId);

    Optional<Vocabulary> findForUpdate(UUID vocabularyId, UUID userId);

    boolean existsByIdAndUser_Id(UUID vocabularyId, UUID userId);

    void increaseWordCount(UUID vocabularyId);

    void decreaseWordCount(UUID vocabularyId);

    void delete(UUID vocabularyId, UUID userId);

    void update(String title, String description, UUID vocabularyId, UUID userId);

    boolean existsByTitleAndUserId(String title, UUID userId);

    boolean existsByTitleAndIdNotAndUserId(String title, UUID vocabularyId, UUID userId);

    boolean existsByUserId(UUID userId);
}
