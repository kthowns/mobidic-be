package com.kimtaeyang.mobidic.dictionary.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.dictionary.dto.AddWordRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDto;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.statistic.repository.WordStatisticRepository;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
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
public class WordService {
    private final WordRepository wordRepository;
    private final VocabularyRepository vocabularyRepository;
    private final WordStatisticRepository wordStatisticRepository;

    @Transactional
    @PreAuthorize("@vocabularyAccessHandler.ownershipCheck(#vocabId)")
    public WordDto addWord(UUID vocabId, AddWordRequestDto request) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        int count = wordRepository.countByExpressionAndVocabulary(request.getExpression(), vocabulary);

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }

        Word word = Word.builder()
                .expression(request.getExpression())
                .vocabulary(vocabulary)
                .build();
        wordRepository.save(word);

        WordStatistic rate = WordStatistic.builder()
                .word(word)
                .correctCount(0)
                .incorrectCount(0)
                .isLearned(true)
                .build();
        wordStatisticRepository.save(rate);

        return WordDto.fromEntity(word);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@vocabularyAccessHandler.ownershipCheck(#vId)")
    public List<WordDto> getWordsByVocabularyId(UUID vId) {
        Vocabulary vocabulary = vocabularyRepository.findById(vId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        return wordRepository.findByVocabulary(vocabulary)
                .stream().map(WordDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public WordDto updateWord(UUID wordId, AddWordRequestDto request) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        long count = wordRepository.countByExpressionAndVocabularyAndIdNot(request.getExpression(), word.getVocabulary(), wordId);

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }

        word.setExpression(request.getExpression());
        wordRepository.save(word);

        return WordDto.fromEntity(word);
    }

    @Transactional
    @PreAuthorize("@wordAccessHandler.ownershipCheck(#wordId)")
    public WordDto deleteWord(UUID wordId) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        wordRepository.delete(word);

        return WordDto.fromEntity(word);
    }
}
