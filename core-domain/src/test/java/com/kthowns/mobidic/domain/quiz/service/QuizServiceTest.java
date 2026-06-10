package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.Quiz;
import com.kthowns.mobidic.domain.quiz.model.QuizAnswer;
import com.kthowns.mobidic.domain.quiz.model.QuizInfo;
import com.kthowns.mobidic.domain.quiz.model.QuizResult;
import com.kthowns.mobidic.domain.quiz.properties.QuizProperties;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.service.WordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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
    private QuizProperties quizProperties;

    @Mock
    private QuizAppender quizAppender;
    @Mock
    private QuizReader quizReader;
    @Mock
    private QuizRemover quizRemover;
    @Mock
    private QuizValidator quizValidator;

    private final UUID userId = UUID.randomUUID();
    private final UUID vocabId = UUID.randomUUID();
    private final UUID wordId = UUID.randomUUID();

    @Test
    @DisplayName("[QuizService] Get OX quizzes success")
    void getOXQuizzesSuccess() {
        // given
        List<WordDetail> wordDetails = List.of(
                new WordDetail(wordId, "apple", 0.5, 0.0, false, List.of(), null),
                new WordDetail(UUID.randomUUID(), "banana", 0.5, 0.0, false, List.of(), null)
        );
        given(wordService.getWordDetailsNotLearnedByVocabularyId(userId, vocabId)).willReturn(wordDetails);
        given(quizProperties.getExpPerQuiz()).willReturn(15000L);
        given(quizAppender.append(any(UUID.class), any(Quiz.class), anyLong())).willReturn("token");

        // when
        List<QuizInfo> result = quizService.getOXQuizzes(userId, vocabId);

        // then
        assertEquals(2, result.size());
        verify(quizAppender, times(2)).append(eq(userId), any(Quiz.class), anyLong());
    }

    @Test
    @DisplayName("[QuizService] Rate quiz success - Correct answer")
    void rateQuizCorrect() {
        // given
        String token = "token";
        String answer = "1";
        QuizAnswer quizAnswer = new QuizAnswer(userId, wordId, UUID.randomUUID(), "1");

        given(quizReader.read(token)).willReturn(quizAnswer);

        // when
        QuizResult result = quizService.rateQuiz(userId, token, answer);

        // then
        assertTrue(result.isCorrect());
        assertEquals("1", result.correctAnswer());
        verify(quizValidator).validateOwnership(quizAnswer, userId);
        verify(quizRemover).remove(token);
        verify(statisticService).increaseCorrectCount(userId, wordId);
    }
}
