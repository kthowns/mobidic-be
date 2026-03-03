package com.kimtaeyang.mobidic.dictionary.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.dictionary.dto.AddWordRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDetail;
import com.kimtaeyang.mobidic.dictionary.dto.WordDto;
import com.kimtaeyang.mobidic.dictionary.entity.Vocabulary;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.repository.VocabularyRepository;
import com.kimtaeyang.mobidic.dictionary.repository.WordRepository;
import com.kimtaeyang.mobidic.statistic.entity.WordStatistic;
import com.kimtaeyang.mobidic.statistic.repository.WordStatisticRepository;
import com.kimtaeyang.mobidic.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;
    private final VocabularyRepository vocabularyRepository;
    private final WordStatisticRepository wordStatisticRepository;

    @Transactional
    public WordDto addWord(User user, UUID vocabId, AddWordRequestDto request) {
        Vocabulary vocabulary = vocabularyRepository
                .findByIdAndUser_Id(vocabId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        // 중복 체크
        if (wordRepository.existsByExpressionAndVocabulary(request.getExpression(), vocabulary)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }

        Word word = Word.builder()
                .expression(request.getExpression())
                .vocabulary(vocabulary)
                .build();
        word = wordRepository.save(word);

        WordStatistic rate = WordStatistic.builder()
                .word(word)
                .correctCount(0)
                .incorrectCount(0)
                .isLearned(false)
                .build();
        wordStatisticRepository.save(rate);

        return WordDto.fromEntity(word);
    }

    @Transactional(readOnly = true)
    public WordDto getWordById(User user, UUID wordId) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        return WordDto.fromEntity(word);
    }

    @Transactional(readOnly = true)
    public List<WordDetail> getWordDetailsByVocabularyId(User user, UUID vocabularyId) {
        if (!vocabularyRepository.existsByIdAndUser_Id(vocabularyId, user.getId())) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        return wordRepository.findWordDetailsByVocabularyId(user.getId(), vocabularyId);
    }

    @Transactional
    public WordDto updateWord(User user, UUID wordId, AddWordRequestDto request) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        long count = wordRepository
                .countByExpressionAndVocabularyAndIdNot(request.getExpression(), word.getVocabulary(), wordId);

        if (count > 0) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }

        word.setExpression(request.getExpression());
        wordRepository.save(word);

        return WordDto.fromEntity(word);
    }

    @Transactional
    public WordDto deleteWord(User user, UUID wordId) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));
        wordRepository.delete(word);
        return WordDto.fromEntity(word);
    }
}
