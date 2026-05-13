package com.kthowns.mobidic.dictionary.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.dictionary.dto.AddVocabularyRequestDto;
import com.kthowns.mobidic.dictionary.dto.VocabularyDetail;
import com.kthowns.mobidic.dictionary.dto.VocabularyDto;
import com.kthowns.mobidic.dictionary.entity.Vocabulary;
import com.kthowns.mobidic.dictionary.repository.VocabularyRepository;
import com.kthowns.mobidic.user.entity.User;
import jakarta.validation.Valid;
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
    private final VocabularyRepository vocabularyRepository;

    @Transactional
    public VocabularyDto addVocabulary(
            User user,
            @Valid AddVocabularyRequestDto request
    ) {
        if (vocabularyRepository.existsByTitleAndUser(request.getTitle(), user)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_TITLE);
        }

        Vocabulary vocabulary = Vocabulary.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();
        vocabulary = vocabularyRepository.save(vocabulary);

        return VocabularyDto.fromEntity(vocabulary);
    }

    @Transactional(readOnly = true)
    public List<VocabularyDetail> getVocabularyDetails(User user) {
        return vocabularyRepository.findVocabularyDetails(user.getId());
    }

    @Transactional(readOnly = true)
    public VocabularyDetail getVocabularyById(User user, UUID vocabularyId) {
        return vocabularyRepository.findVocabularyDetail(vocabularyId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));
    }

    @Transactional
    public VocabularyDto updateVocabulary(
            User user,
            UUID vocabularyId,
            AddVocabularyRequestDto request
    ) {
        Vocabulary vocabulary = vocabularyRepository.findForUpdate(vocabularyId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        if (vocabularyRepository.existsByTitleAndUserAndIdNot(request.getTitle(), vocabulary.getUser(), vocabularyId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_TITLE);
        }

        vocabulary.setTitle(request.getTitle());
        vocabulary.setDescription(request.getDescription());
        vocabulary = vocabularyRepository.save(vocabulary);

        return VocabularyDto.fromEntity(vocabulary);
    }

    @Transactional
    public VocabularyDto deleteVocab(
            User user,
            UUID vocabularyId
    ) {
        Vocabulary vocabulary = vocabularyRepository.findByIdAndUser_Id(vocabularyId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        vocabularyRepository.delete(vocabulary);

        return VocabularyDto.fromEntity(vocabulary);
    }
}