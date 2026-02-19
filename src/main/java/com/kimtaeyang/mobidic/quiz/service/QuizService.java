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
import com.kimtaeyang.mobidic.quiz.dto.QuizStatisticDto;
import com.kimtaeyang.mobidic.quiz.model.Quiz;
import com.kimtaeyang.mobidic.quiz.type.QuizType;
import com.kimtaeyang.mobidic.quiz.util.QuizGenerator;
import com.kimtaeyang.mobidic.quiz.util.QuizGeneratorFactory;
import com.kimtaeyang.mobidic.statistic.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private static final String QUESTION_PREFIX = "question";
    private final RedisTemplate<String, String> redisTemplate;
    private static final Long expPerQuestion = 15000L;
    private final VocabularyService vocabularyService;
    private final StatisticService statisticService;
    private final CryptoService cryptoService;
    private final DefinitionService definitionService;

    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vocabularyId)")
    public List<QuizDto> getOXQuizzes(UUID vocabularyId) {
        return generateQuestions(vocabularyId, QuizType.OX);
    }

    @PreAuthorize("@vocabAccessHandler.ownershipCheck(#vocabularyId)")
    public List<QuizDto> getBlankQuizzes(UUID vocabularyId) {
        return generateQuestions(vocabularyId, QuizType.BLANK);
    }

    @PreAuthorize("@userAccessHandler.ownershipCheck(#userId)")
    public QuizStatisticDto.Response rateQuestion(
            UUID userId,
            QuizStatisticDto.Request request
    ) {
        String key = cryptoService.decrypt(request.getToken()); //복호화
        String correctAnswer = findCorrectAnswer(key);
        expireAnswer(key);

        UUID wordId = UUID.fromString(key.split(":")[2]);
        QuizStatisticDto.Response response = QuizStatisticDto.Response.builder()
                .isCorrect(request.getAnswer().equals(correctAnswer))
                .correctAnswer(correctAnswer)
                .build();

        if (response.getIsCorrect()) {
            statisticService.increaseCorrectCount(wordId);
        } else {
            statisticService.increaseIncorrectCount(wordId);
        }

        return response;
    }

    private List<QuizDto> generateQuestions(UUID vocabularyId, QuizType quizType) {
        VocabularyDto vocabulary = vocabularyService.getVocabById(vocabularyId);

        List<WordWithDefinitions> wordsWithDefs = new ArrayList<>();
        List<WordDto> wordDtos = wordService.getWordsByVocabId(vocabulary.getId());
        if (wordDtos.isEmpty()) {
            return List.of();
        }

        for (WordDto wordDto : wordDtos) {
            WordWithDefinitions wordWithDefinitions = WordWithDefinitions.builder()
                    .wordDto(wordDto)
                    .definitionDtos(definitionService.getDefinitionsByWordId(wordDto.getId()))
                    .build();

            wordsWithDefs.add(wordWithDefinitions);
        }

        QuizGenerator quizGenerator = QuizGeneratorFactory.get(quizType);

        List<Quiz> quizzes = quizGenerator.generate(vocabulary.getUserId(), wordsWithDefs);
        List<QuizDto> quizDtos = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            long expSec = expPerQuestion * quizzes.size();
            String token = registerQuestion(quiz, expSec);
            quizDtos.add(QuizDto.builder()
                    .token(token)
                    .options(quiz.getOptions())
                    .stem(quiz.getStem())
                    .expMil(expSec)
                    .build());
        }

        return quizDtos;
    }

    private String registerQuestion(Quiz quiz, long expSec) {
        String key = QUESTION_PREFIX
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
        validateQuestion(token);
        String correctAnswer = redisTemplate.opsForValue().get(token);
        if (correctAnswer == null) {
            throw new ApiException(GeneralResponseCode.REQUEST_TIMEOUT);
        }

        return correctAnswer;
    }

    private void expireAnswer(String token) {
        redisTemplate.delete(token);
    }

    private void validateQuestion(String token) {
        if (!token.split(":")[0].equals(QUESTION_PREFIX)) {
            throw new ApiException(GeneralResponseCode.INVALID_REQUEST);
        }
        if (!redisTemplate.hasKey(token)) {
            throw new ApiException(GeneralResponseCode.REQUEST_TIMEOUT);
        }
    }
}