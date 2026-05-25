package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.quiz.implementation.*;
import com.kthowns.mobidic.domain.quiz.model.Quiz;
import com.kthowns.mobidic.domain.quiz.model.QuizInfo;
import com.kthowns.mobidic.domain.quiz.model.QuizResult;
import com.kthowns.mobidic.domain.quiz.model.QuizType;
import com.kthowns.mobidic.domain.quiz.util.QuizGenerator;
import com.kthowns.mobidic.domain.quiz.util.QuizGeneratorFactory;
import com.kthowns.mobidic.domain.statistic.service.StatisticService;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {
    private final WordService wordService;
    private final StatisticService statisticService;

    private final QuizAppender quizAppender;
    private final QuizReader quizReader;
    private final QuizRemover quizRemover;
    private final QuizValidator quizValidator;
    private final QuizProcessor quizProcessor;

    private static final String QUIZ_PREFIX = "quiz";
    private static final Long expPerQuiz = 15000L;

    @Transactional(readOnly = true)
    public List<QuizInfo> getOXQuizzes(UUID userId, UUID vocabularyId) {
        return generateQuizzes(userId, vocabularyId, QuizType.OX);
    }

    @Transactional(readOnly = true)
    public List<QuizInfo> getBlankQuizzes(UUID userId, UUID vocabularyId) {
        return generateQuizzes(userId, vocabularyId, QuizType.BLANK);
    }

    @Transactional
    public QuizResult rateQuiz(UUID userId, String token, String answer) {
        String key = quizProcessor.decryptKey(token);
        quizValidator.validateQuizKey(key);

        String[] parts = key.split(":");
        UUID tokenUserId = UUID.fromString(parts[1]);
        UUID wordId = UUID.fromString(parts[2]);

        if (!tokenUserId.equals(userId)) {
            throw new ApiException(GeneralResponseCode.NO_QUIZ);
        }

        String correctAnswer = quizReader.readAnswer(key);
        quizRemover.removeAnswer(key);

        boolean isCorrect = answer.equalsIgnoreCase(correctAnswer);

        if (isCorrect) {
            statisticService.increaseCorrectCount(userId, wordId);
        } else {
            statisticService.increaseIncorrectCount(userId, wordId);
        }

        return QuizResult.builder()
                .isCorrect(isCorrect)
                .correctAnswer(correctAnswer)
                .build();
    }

    private List<QuizInfo> generateQuizzes(UUID userId, UUID vocabularyId, QuizType quizType) {
        List<WordDetail> wordDetails = wordService.getWordDetailsNotLearnedByVocabularyId(userId, vocabularyId);

        if (wordDetails.isEmpty()) {
            return List.of();
        }

        QuizGenerator quizGenerator = QuizGeneratorFactory.get(quizType);
        List<Quiz> quizzes = quizGenerator.generate(userId, wordDetails);
        List<QuizInfo> quizInfos = new ArrayList<>();

        long expSec = expPerQuiz * quizzes.size();

        for (Quiz quiz : quizzes) {
            String key = QUIZ_PREFIX + ":" + quiz.userId() + ":" + quiz.wordId() + ":" + quiz.id();
            quizAppender.saveAnswer(key, quiz.answer(), expSec);

            quizInfos.add(QuizInfo.builder()
                    .token(quizProcessor.encryptKey(key))
                    .options(quiz.options())
                    .stem(quiz.stem())
                    .expMil(expSec)
                    .build());
        }

        return quizInfos;
    }
}