package com.kimtaeyang.mobidic.dictionary.service;

import com.kimtaeyang.mobidic.common.code.AuthResponseCode;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.dictionary.dto.AddVocabularyRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDto;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
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
    private final UserRepository userRepository;

    @Transactional
    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public VocabularyDto addVocabulary(
            UUID userId,
            @Valid AddVocabularyRequestDto request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_MEMBER));

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
    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public List<VocabularyDto> getVocabulariesByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(AuthResponseCode.NO_MEMBER));

        return vocabularyRepository.findByUser(user)
                .stream().map(VocabularyDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vId)")
    public VocabularyDto getVocabById(UUID vId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        return VocabularyDto.fromEntity(vocabulary);
    }

    @Transactional
    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vocabId)")
    public VocabularyDto updateVocab(
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
    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vocabId)")
    public VocabularyDto deleteVocab(UUID vocabId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        vocabularyRepository.delete(vocabulary);

        return VocabularyDto.fromEntity(vocabulary);
    }
}