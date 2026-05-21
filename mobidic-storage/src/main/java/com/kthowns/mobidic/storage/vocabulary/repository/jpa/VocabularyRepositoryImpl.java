package com.kthowns.mobidic.storage.vocabulary.repository.jpa;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VocabularyRepositoryImpl implements VocabularyRepository {
    private final VocabularyJpaRepository vocabularyJpaRepository;

    @Override
    public List<VocabularyDetail> readDetailsByUserId(UUID userId) {
        return vocabularyJpaRepository.findVocabularyDetails(userId);
    }

    @Override
    public void append(Vocabulary vocabulary) {
        VocabularyJpaEntity vocabularyJpaEntity = VocabularyJpaEntity.builder()
                .title(vocabulary.getTitle())
                .description(vocabulary.getDescription())
                .user(UserJpaEntity.builder().id(vocabulary.getUserId()).build())
                .build();
        vocabularyJpaRepository.save(vocabularyJpaEntity);
    }

    @Override
    public Optional<VocabularyDetail> readDetailById(UUID vocabularyId, UUID userId) {
        return vocabularyJpaRepository.findVocabularyDetail(vocabularyId, userId);
    }

    @Override
    public void delete(UUID vocabularyId, UUID userId) {
        VocabularyJpaEntity vocabularyJpaEntity = vocabularyJpaRepository.findByIdAndUser_Id(vocabularyId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
        vocabularyJpaRepository.delete(vocabularyJpaEntity);
    }

    @Override
    @Transactional
    public void update(String title, String description, UUID vocabularyId, UUID userId) {
        VocabularyJpaEntity vocabularyJpaEntity = vocabularyJpaRepository.findForUpdate(vocabularyId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        vocabularyJpaEntity.setTitle(title);
        vocabularyJpaEntity.setDescription(description);
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
