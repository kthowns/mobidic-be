package com.kthowns.mobidic.domain.vocabulary.service;

import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VocabularyService {
    private final VocabularyValidator vocabularyValidator;
    private final VocabularyAppender vocabularyAppender;
    private final VocabularyReader vocabularyReader;
    private final VocabularyUpdater vocabularyUpdater;
    private final VocabularyRemover vocabularyRemover;

    @Transactional
    public Vocabulary addVocabulary(
            UUID userId,
            String title,
            String description
    ) {
        // 동시성 제어 필요
        vocabularyValidator.validateTitleAppendDuplication(title, userId);
        return vocabularyAppender.append(title, description, userId);
    }

    @Transactional(readOnly = true)
    public List<VocabularyDetail> getVocabularyDetails(UUID userId) {
        return vocabularyReader.readDetailsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public VocabularyDetail getVocabularyById(UUID userId, UUID vocabularyId) {
        return vocabularyReader.readDetailById(userId, vocabularyId);
    }

    @Transactional
    public void updateVocabulary(
            UUID userId,
            UUID vocabularyId,
            String title,
            String description
    ) {
        vocabularyValidator.validateTitleUpdateDuplication(title, vocabularyId, userId);
        vocabularyUpdater.update(userId, vocabularyId, title, description);
    }

    @Transactional
    public void deleteVocab(
            UUID userId,
            UUID vocabularyId
    ) {
        vocabularyRemover.remove(vocabularyId, userId);
    }

    @Transactional(readOnly = true)
    public boolean existsByIdAndUser(UUID vocabularyId, UUID userId) {
        return vocabularyReader.existsByIdAndUser(vocabularyId, userId);
    }

    @Transactional(readOnly = true)
    public boolean existsByUser(UUID userId) {
        return vocabularyReader.existsByUser(userId);
    }

    @Transactional
    public void increaseWordCount(UUID vocabularyId) {
        vocabularyUpdater.increaseWordCount(vocabularyId);
    }

    @Transactional
    public void decreaseWordCount(UUID vocabularyId) {
        vocabularyUpdater.decreaseWordCount(vocabularyId);
    }
}