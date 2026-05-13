package com.kthowns.mobidic.api.quiz.service;

import com.kthowns.mobidic.api.common.code.GeneralResponseCode;
import com.kthowns.mobidic.api.common.exception.ApiException;
import com.kthowns.mobidic.api.dictionary.dto.WordDetail;
import com.kthowns.mobidic.api.dictionary.service.WordService;
import com.kthowns.mobidic.api.quiz.dto.QuizDto;
import com.kthowns.mobidic.api.quiz.dto.QuizRateRequest;
import com.kthowns.mobidic.api.quiz.dto.QuizRateResponse;
import com.kthowns.mobidic.api.quiz.model.Quiz;
import com.kthowns.mobidic.api.quiz.type.QuizType;
import com.kthowns.mobidic.api.quiz.util.QuizGenerator;
import com.kthowns.mobidic.api.quiz.util.QuizGeneratorFactory;
import com.kthowns.mobidic.api.statistic.service.StatisticService;
import com.kthowns.mobidic.api.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {
    private final WordService wordService;
    private static final String QUIZ_PREFIX = "quiz";
    private final RedisTemplate<String, String> redisTemplate;
    private static final Long expPerQuiz = 15000L;
    private final StatisticService statisticService;
    private final CryptoService cryptoService;

    @Transactional(readOnly = true)
    public List<QuizDto> getOXQuizzes(
            User user,
            UUID vocabularyId
    ) {
        return generateQuizzes(user, vocabularyId, QuizType.OX);
    }

    @Transactional(readOnly = true)
    public List<QuizDto> getBlankQuizzes(
            User user,
            UUID vocabularyId
    ) {
        return generateQuizzes(user, vocabularyId, QuizType.BLANK);
    }

    @Transactional
    public QuizRateResponse rateQuiz(
            User user,
            QuizRateRequest quizRateRequest
    ) {
        /*
            Request token validation 필요!!!
         */
        String plainToken = cryptoService.decrypt(quizRateRequest.getToken());
        UUID userId = UUID.fromString(plainToken.split(":")[1]);

        // quiz:{userId}:{wordId}:{quizId}
        if (!userId.equals(user.getId())) {
            throw new ApiException(GeneralResponseCode.NO_QUIZ);
        }

        String key = cryptoService.decrypt(quizRateRequest.getToken()); //복호화
        String correctAnswer = findCorrectAnswer(key);
        expireAnswer(key);

        UUID wordId = UUID.fromString(key.split(":")[2]);
        QuizRateResponse quizRateResponse = QuizRateResponse.builder()
                .isCorrect(quizRateRequest.getAnswer().equalsIgnoreCase(correctAnswer))
                .correctAnswer(correctAnswer)
                .build();

        if (quizRateResponse.getIsCorrect()) {
            statisticService.increaseCorrectCount(user, wordId);
        } else {
            statisticService.increaseIncorrectCount(user, wordId);
        }

        return quizRateResponse;
    }

    private List<QuizDto> generateQuizzes(
            User user,
            UUID vocabularyId,
            QuizType quizType
    ) {
        List<WordDetail> wordDetails = wordService.getWordDetailsNotLearnedByVocabularyId(user, vocabularyId);

        if (wordDetails.isEmpty()) {
            return List.of();
        }

        QuizGenerator quizGenerator = QuizGeneratorFactory.get(quizType);

        List<Quiz> quizzes = quizGenerator.generate(user.getId(), wordDetails);
        List<QuizDto> quizDtos = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            long expSec = expPerQuiz * quizzes.size();
            String token = registerQuiz(quiz, expSec);
            quizDtos.add(QuizDto.builder()
                    .token(token)
                    .options(quiz.getOptions())
                    .stem(quiz.getStem())
                    .expMil(expSec)
                    .build());
        }

        return quizDtos;
    }

    private String registerQuiz(Quiz quiz, long expSec) {
        // quiz:{userId}:{wordId}:{quizId}
        String key = QUIZ_PREFIX
                + ":" + quiz.getUserId()
                + ":" + quiz.getWordId()
                + ":" + quiz.getId();

        redisTemplate.opsForValue().set(
                key,
                quiz.getAnswer(),
                Duration.ofMillis(expSec)
        );

        return cryptoService.encrypt(key); //암호화
    }

    private String findCorrectAnswer(String token) {
        validateQuiz(token);
        String correctAnswer = redisTemplate.opsForValue().get(token);
        if (correctAnswer == null) {
            throw new ApiException(GeneralResponseCode.REQUEST_TIMEOUT);
        }

        return correctAnswer;
    }

    private void expireAnswer(String token) {
        redisTemplate.delete(token);
    }

    private void validateQuiz(String token) {
        if (!token.split(":")[0].equals(QUIZ_PREFIX)) {
            throw new ApiException(GeneralResponseCode.INVALID_REQUEST);
        }
        if (!redisTemplate.hasKey(token)) {
            throw new ApiException(GeneralResponseCode.REQUEST_TIMEOUT);
        }
    }
}