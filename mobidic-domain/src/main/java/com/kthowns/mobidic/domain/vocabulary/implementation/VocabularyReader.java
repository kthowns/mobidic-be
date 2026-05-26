package com.kthowns.mobidic.domain.vocabulary.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VocabularyReader {
    private final VocabularyRepository vocabularyRepository;

    public List<VocabularyDetail> readDetailsByUserId(UUID userId) {
        return vocabularyRepository.readDetailsByUserId(userId);
    }

    public Vocabulary readById(UUID vocabularyId, UUID userId) {
        return vocabularyRepository.readByIdAndUserId(vocabularyId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
    }

    public VocabularyDetail readDetailById(UUID userId, UUID vocabularyId) {
        return vocabularyRepository.readDetailById(vocabularyId, userId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
    }

    public boolean existsByUser(UUID userId) {
        return vocabularyRepository.existsByUserId(userId);
    }

    public boolean existsByIdAndUser(UUID vocabularyId, UUID userId) {
        return vocabularyRepository.existsByIdAndUser_Id(vocabularyId, userId);
    }
}
