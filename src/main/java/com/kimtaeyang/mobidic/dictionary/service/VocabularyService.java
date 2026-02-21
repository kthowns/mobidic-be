package com.kimtaeyang.mobidic.dictionary.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.dictionary.dto.AddVocabularyRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDto;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        int count = vocabularyRepository.countByTitleAndUser(request.getTitle(), user);

        if (count > 0) {
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
    public List<VocabularyDto> getVocabularies(User user) {
        return vocabularyRepository.findByUser(user)
                .stream().map(VocabularyDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@vocabularyAccessHandler.ownershipCheck(#vocabularyId)")
    public VocabularyDto getVocabularyById(UUID vocabularyId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        return VocabularyDto.fromEntity(vocabulary);
    }

    @Transactional
    @PreAuthorize("@vocabularyAccessHandler.ownershipCheck(#vocabId)")
    public VocabularyDto updateVocabulary(
            UUID vocabId, AddVocabularyRequestDto request) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        long count = vocabularyRepository.countByTitleAndUserAndIdNot(request.getTitle(), vocabulary.getUser(), vocabId);

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_TITLE);
        }

        vocabulary.setTitle(request.getTitle());
        vocabulary.setDescription(request.getDescription());
        vocabulary = vocabularyRepository.save(vocabulary);

        return VocabularyDto.fromEntity(vocabulary);
    }

    @Transactional
    @PreAuthorize("@vocabularyAccessHandler.ownershipCheck(#vocabId)")
    public VocabularyDto deleteVocab(UUID vocabId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        vocabularyRepository.delete(vocabulary);

        return VocabularyDto.fromEntity(vocabulary);
    }
}