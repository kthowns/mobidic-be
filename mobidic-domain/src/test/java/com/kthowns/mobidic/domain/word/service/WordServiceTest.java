package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.domain.vocabulary.service.VocabularyService;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {
    @InjectMocks
    private WordService wordService;

    @Mock
    private WordReader wordReader;
    @Mock
    private WordAppender wordAppender;
    @Mock
    private WordUpdater wordUpdater;
    @Mock
    private WordRemover wordRemover;
    @Mock
    private WordValidator wordValidator;

    @Mock
    private VocabularyService vocabularyService;

    private final UUID userId = UUID.randomUUID();
    private final UUID vocabId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();

    @Test
    @DisplayName("[WordService] Add word success")
    void addWordSuccess() {
        // given
        String expression = "testWord";
        Word word = new Word(wordId, vocabId, expression, null);

        given(vocabularyService.existsByIdAndUser(vocabId, userId)).willReturn(true);
        given(wordAppender.append(expression, vocabId)).willReturn(word);

        // when
        Word result = wordService.addWord(userId, vocabId, expression);

        // then
        verify(wordAppender).append(expression, vocabId);
        verify(wordValidator).validateExpressionDuplication(expression, vocabId, userId);
        assertEquals(word, result);
    }

    @Test
    @DisplayName("[WordService] Get word by id success")
    void getWordByIdSuccess() {
        // given
        Word word = new Word(wordId, vocabId, "expression", null);
        given(wordReader.readByIdAndUserId(wordId, userId)).willReturn(word);

        // when
        Word result = wordService.getWordById(userId, wordId);

        // then
        assertEquals(word, result);
    }

    @Test
    @DisplayName("[WordService] Get word details success")
    void getWordDetailsByVocabularyIdSuccess() {
        // given
        List<WordDetail> wordDetails = List.of(
                new WordDetail(wordId, "expression", 0, 0, false, List.of(), null)
        );
        given(vocabularyService.existsByIdAndUser(vocabId, userId)).willReturn(true);
        given(wordReader.readDetailsByVocabularyId(userId, vocabId, false)).willReturn(wordDetails);

        // when
        List<WordDetail> result = wordService.getWordDetailsByVocabularyId(userId, vocabId);

        // then
        assertEquals(wordDetails, result);
    }

    @Test
    @DisplayName("[WordService] Update word success")
    void updateWordSuccess() {
        // given
        String newExpression = "newExpression";
        Word existingWord = new Word(wordId, vocabId, "oldExpression", null);
        given(wordReader.readByIdAndUserId(wordId, userId)).willReturn(existingWord);

        // when
        wordService.updateWord(userId, wordId, newExpression);

        // then
        verify(wordValidator).validateExpressionDuplicationForUpdate(newExpression, vocabId, wordId, userId);
        verify(wordUpdater).update(userId, wordId, newExpression);
    }

    @Test
    @DisplayName("[WordService] Delete word success")
    void deleteWordSuccess() {
        // given
        Word word = new Word(wordId, vocabId, "expression", null);
        given(wordReader.readByIdAndUserId(wordId, userId)).willReturn(word);

        // when
        wordService.deleteWord(userId, wordId);

        // then
        verify(wordRemover).remove(wordId, userId);
        verify(vocabularyService).decreaseWordCount(vocabId, userId);
    }
}
