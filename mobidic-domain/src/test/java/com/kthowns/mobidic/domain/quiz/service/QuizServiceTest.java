package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.quiz.implementation.*;
import com.kthowns.mobidic.domain.quiz.model.QuizInfo;
import com.kthowns.mobidic.domain.quiz.model.QuizResult;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.service.WordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {
    @InjectMocks
    private QuizService quizService;

    @Mock
    private WordService wordService;
    @Mock
    private StatisticService statisticService;
    @Mock
    private QuizAppender quizAppender;
    @Mock
    private QuizReader quizReader;
    @Mock
    private QuizRemover quizRemover;
    @Mock
    private QuizValidator quizValidator;
    @Mock
    private QuizProcessor quizProcessor;

    private final UUID userId = UUID.randomUUID();
    private final UUID vocabId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();

    @Test
    @DisplayName("[QuizService] Get OX quizzes success")
    void getOXQuizzesSuccess() {
        // given
        List<WordDetail> wordDetails = List.of(
                new WordDetail(wordId, "Apple", 0, 0, false, List.of(new Definition(UUID.randomUUID(), wordId, "사과", PartOfSpeech.NOUN)), LocalDateTime.now())
        );
        given(wordService.getWordDetailsNotLearnedByVocabularyId(userId, vocabId)).willReturn(wordDetails);
        given(quizProcessor.encryptKey(anyString())).willReturn("encryptedKey");

        // when
        List<QuizInfo> result = quizService.getOXQuizzes(userId, vocabId);

        // then
        assertEquals(1, result.size());
        verify(quizAppender).saveAnswer(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("[QuizService] Rate quiz success")
    void rateQuizSuccess() {
        // given
        String token = "testToken";
        String answer = "사과";
        String key = "quiz:" + userId + ":" + wordId + ":quizId";
        
        given(quizProcessor.decryptKey(token)).willReturn(key);
        given(quizReader.readAnswer(key)).willReturn(answer);

        // when
        QuizResult result = quizService.rateQuiz(userId, token, answer);

        // then
        assertTrue(result.isCorrect());
        assertEquals(answer, result.correctAnswer());
        verify(quizValidator).validateQuizKey(key);
        verify(quizRemover).removeAnswer(key);
        verify(statisticService).increaseCorrectCount(userId, wordId);
    }
}
