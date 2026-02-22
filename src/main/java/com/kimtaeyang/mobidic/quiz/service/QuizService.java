package com.kimtaeyang.mobidic.quiz.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDto;
import com.kimtaeyang.mobidic.dictionary.model.WordWithDefinitions;
import com.kimtaeyang.mobidic.dictionary.service.DefinitionService;
import com.kimtaeyang.mobidic.dictionary.service.VocabularyService;
import com.kimtaeyang.mobidic.dictionary.service.WordService;
import com.kimtaeyang.mobidic.quiz.dto.QuizDto;
import com.kimtaeyang.mobidic.quiz.dto.QuizRateRequest;
import com.kimtaeyang.mobidic.quiz.dto.QuizRateResponse;
import com.kimtaeyang.mobidic.quiz.model.Quiz;
import com.kimtaeyang.mobidic.quiz.type.QuizType;
import com.kimtaeyang.mobidic.quiz.util.QuizGenerator;
import com.kimtaeyang.mobidic.quiz.util.QuizGeneratorFactory;
import com.kimtaeyang.mobidic.statistic.service.StatisticService;
import com.kimtaeyang.mobidic.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    private final VocabularyService vocabularyService;
    private final StatisticService statisticService;
    private final CryptoService cryptoService;
    private final DefinitionService definitionService;

    public List<QuizDto> getOXQuizzes(
            User user,
            UUID vocabularyId
    ) {
        return generateQuizs(user, vocabularyId, QuizType.OX);
    }

    public List<QuizDto> getBlankQuizzes(
            User user,
            UUID vocabularyId
    ) {
        return generateQuizs(user, vocabularyId, QuizType.BLANK);
    }

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
                .isCorrect(quizRateRequest.getAnswer().equals(correctAnswer))
                .correctAnswer(correctAnswer)
                .build();

        if (quizRateResponse.getIsCorrect()) {
            statisticService.increaseCorrectCount(wordId);
        } else {
            statisticService.increaseIncorrectCount(wordId);
        }

        return quizRateResponse;
    }

    private List<QuizDto> generateQuizs(
            User user,
            UUID vocabularyId,
            QuizType quizType
    ) {
        VocabularyDto vocabulary = vocabularyService.getVocabularyById(user, vocabularyId);

        List<WordWithDefinitions> wordsWithDefs = new ArrayList<>();
        List<WordDto> wordDtos = wordService.getWordsByVocabularyId(user, vocabularyId);
        if (wordDtos.isEmpty()) {
            return List.of();
        }

        for (WordDto wordDto : wordDtos) {
            WordWithDefinitions wordWithDefinitions = WordWithDefinitions.builder()
                    .wordDto(wordDto)
                    .definitionDtos(definitionService.getDefinitionsByWordId(user, wordDto.getId()))
                    .build();

            wordsWithDefs.add(wordWithDefinitions);
        }

        QuizGenerator quizGenerator = QuizGeneratorFactory.get(quizType);

        List<Quiz> quizzes = quizGenerator.generate(vocabulary.getUserId(), wordsWithDefs);
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