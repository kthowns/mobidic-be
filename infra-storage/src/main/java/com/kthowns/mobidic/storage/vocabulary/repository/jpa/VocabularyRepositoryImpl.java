package com.kthowns.mobidic.storage.vocabulary.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VocabularyRepositoryImpl implements VocabularyRepository {
    private final VocabularyJpaRepository vocabularyJpaRepository;
    private final EntityManager em;

    @Override
    public List<VocabularyDetail> readDetailsByUserId(UUID userId) {
        return vocabularyJpaRepository.findVocabularyDetails(userId);
    }

    @Override
    public Vocabulary append(Vocabulary vocabulary) {
        UserJpaEntity user = em.getReference(UserJpaEntity.class, vocabulary.userId());
        VocabularyJpaEntity vocabularyJpaEntity = VocabularyJpaEntity.fromModel(vocabulary, user);
        return vocabularyJpaRepository.save(vocabularyJpaEntity).toModel();
    }

    @Override
    public Optional<Vocabulary> readByIdAndUserId(UUID vocabularyId, UUID userId) {
        return vocabularyJpaRepository.findByIdAndUser_Id(vocabularyId, userId)
                .map(VocabularyJpaEntity::toModel);
    }

    @Override
    public Optional<VocabularyDetail> readDetailById(UUID vocabularyId, UUID userId) {
        return vocabularyJpaRepository.findVocabularyDetail(vocabularyId, userId);
    }

    @Override
    public boolean existsByIdAndUser_Id(UUID vocabularyId, UUID userId) {
        return vocabularyJpaRepository.existsByIdAndUser_Id(vocabularyId, userId);
    }

    @Override
    public void delete(UUID vocabularyId, UUID userId) {
        VocabularyJpaEntity vocabularyJpaEntity = vocabularyJpaRepository.findByIdAndUser_Id(vocabularyId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
        vocabularyJpaRepository.delete(vocabularyJpaEntity);
    }

    @Override
    public void increaseWordCount(UUID vocabularyId, UUID userId) {
        vocabularyJpaRepository.increaseWordCount(vocabularyId, userId);
    }

    @Override
    public void decreaseWordCount(UUID vocabularyId, UUID userId) {
        vocabularyJpaRepository.decreaseWordCount(vocabularyId, userId);
    }

    @Override
    public void update(Vocabulary vocabulary) {
        VocabularyJpaEntity vocabularyJpaEntity = vocabularyJpaRepository.findForUpdate(vocabulary.id(), vocabulary.userId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        vocabularyJpaEntity.updateFromModel(vocabulary);
    }

    @Override
    public boolean existsByTitleAndUserId(String title, UUID userId) {
        return vocabularyJpaRepository.existsByTitleAndUser_Id(title, userId);
    }

    @Override
    public boolean existsByTitleAndIdNotAndUserId(String title, UUID vocabularyId, UUID userId) {
        return vocabularyJpaRepository.existsByTitleAndIdNotAndUser_Id(title, vocabularyId, userId);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return vocabularyJpaRepository.existsByUser_Id(userId);
    }
}
