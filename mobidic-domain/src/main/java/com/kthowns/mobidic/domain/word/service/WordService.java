package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
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
    public Word addWord(User user, UUID vocabId, AddWordRequestDto request) {
        Vocabulary vocabulary = vocabularyRepository
                .findForUpdate(vocabId, user.getId())
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
                .isLearned(false)
                .build();
        wordStatisticRepository.save(rate);

        vocabulary.addWordCount();

        return Word.fromEntity(word);
    }

    @Transactional(readOnly = true)
    public Word getWordById(User user, UUID wordId) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        return Word.fromEntity(word);
    }

    @Transactional(readOnly = true)
    public List<WordDetail> getWordDetailsByVocabularyId(User user, UUID vocabularyId) {
        if (!vocabularyRepository.existsByIdAndUser_Id(vocabularyId, user.getId())) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        return wordRepository.findWordDetailsByVocabularyId(user.getId(), vocabularyId, false);
    }

    @Transactional(readOnly = true)
    public List<WordDetail> getWordDetailsNotLearnedByVocabularyId(User user, UUID vocabularyId) {
        if (!vocabularyRepository.existsByIdAndUser_Id(vocabularyId, user.getId())) {
            throw new ApiException(GeneralResponseCode.NO_VOCAB);
        }

        return wordRepository.findWordDetailsByVocabularyId(user.getId(), vocabularyId, true);
    }

    @Transactional
    public Word updateWord(User user, UUID wordId, AddWordRequestDto request) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        if (wordRepository
                .existsByExpressionAndVocabularyAndIdNot(request.getExpression(), word.getVocabulary(), wordId)) {
            throw new ApiException(GeneralResponseCode.DUPLICATED_WORD);
        }

        word.setExpression(request.getExpression());
        wordRepository.save(word);

        return Word.fromEntity(word);
    }

    @Transactional
    public Word deleteWord(User user, UUID wordId) {
        Word word = wordRepository
                .findByIdAndVocabulary_User_Id(wordId, user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_WORD));

        Vocabulary vocabulary = vocabularyRepository.findForUpdate(word.getVocabulary().getId(), user.getId())
                .orElseThrow(() -> new ApiException(GeneralResponseCode.NO_VOCAB));

        wordRepository.delete(word);

        vocabulary.removeWordCount();
        return Word.fromEntity(word);
    }
}
