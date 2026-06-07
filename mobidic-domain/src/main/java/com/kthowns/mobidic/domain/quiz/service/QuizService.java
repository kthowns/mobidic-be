package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.*;
import com.kthowns.mobidic.domain.quiz.properties.QuizProperties;
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
    private final QuizProperties quizProperties;

    private final QuizAppender quizAppender;
    private final QuizReader quizReader;
    private final QuizRemover quizRemover;
    private final QuizValidator quizValidator;

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
        QuizAnswer quizAnswer = quizReader.read(token);

        quizValidator.validateOwnership(quizAnswer, userId);

        quizRemover.remove(token);

        boolean isCorrect = answer.equalsIgnoreCase(quizAnswer.answer());

        if (isCorrect) {
            statisticService.increaseCorrectCount(userId, quizAnswer.wordId());
        } else {
            statisticService.increaseIncorrectCount(userId, quizAnswer.wordId());
        }

        return QuizResult.builder()
                .isCorrect(isCorrect)
                .correctAnswer(quizAnswer.answer())
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

        long expMillis = quizProperties.getExpPerQuiz() * quizzes.size();

        for (Quiz quiz : quizzes) {
            String token = quizAppender.append(userId, quiz, expMillis);

            quizInfos.add(QuizInfo.builder()
                    .token(token)
                    .options(quiz.options())
                    .stem(quiz.stem())
                    .expMil(expMillis)
                    .build());
        }

        return quizInfos;
    }
}
