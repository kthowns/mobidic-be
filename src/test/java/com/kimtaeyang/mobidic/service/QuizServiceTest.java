package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.config.ServiceTestConfig;
import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDto;
import com.kimtaeyang.mobidic.dictionary.model.WordWithDefinitions;
import com.kimtaeyang.mobidic.dictionary.service.DefinitionService;
import com.kimtaeyang.mobidic.dictionary.service.VocabularyService;
import com.kimtaeyang.mobidic.dictionary.service.WordService;
import com.kimtaeyang.mobidic.dictionary.type.PartOfSpeech;
import com.kimtaeyang.mobidic.quiz.dto.QuizDto;
import com.kimtaeyang.mobidic.quiz.dto.QuizRateRequest;
import com.kimtaeyang.mobidic.quiz.dto.QuizRateResponse;
import com.kimtaeyang.mobidic.quiz.service.CryptoService;
import com.kimtaeyang.mobidic.quiz.service.QuizService;
import com.kimtaeyang.mobidic.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {QuizService.class, ServiceTestConfig.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=f825308ac5df56907db5835775baf3e4594526f127cb8d9bca70b435d596d424",
        "jwt.exp=3600000"
})
public class QuizServiceTest {
    @Autowired
    private QuizService quizService;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private WordService wordService;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final User testUser = User.builder()
            .id(UUID.randomUUID())
            .build();

    List<WordWithDefinitions> wordsWithDefs = List.of(
            WordWithDefinitions.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Apple")
                                    .build()
                    )
                    .definitionDtos(List.of(
                                    new DefinitionDto(UUID.randomUUID(), UUID.randomUUID(), "사과", PartOfSpeech.NOUN)
                            )
                    ).build(),
            WordWithDefinitions.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Hello")
                                    .build()
                    )
                    .definitionDtos(List.of(
                                    new DefinitionDto(UUID.randomUUID(), UUID.randomUUID(), "안녕", PartOfSpeech.INTERJECTION)
                            )
                    ).build(),
            WordWithDefinitions.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Run")
                                    .build()
                    )
                    .definitionDtos(List.of(
                                    new DefinitionDto(UUID.randomUUID(), UUID.randomUUID(), "뛰다", PartOfSpeech.VERB)
                            )
                    ).build(),
            WordWithDefinitions.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Idiot")
                                    .build()
                    )
                    .definitionDtos(List.of(
                                    new DefinitionDto(UUID.randomUUID(), UUID.randomUUID(), "바보", PartOfSpeech.NOUN)
                            )
                    ).build(), WordWithDefinitions.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Media")
                                    .build()
                    )
                    .definitionDtos(List.of(
                                    new DefinitionDto(UUID.randomUUID(), UUID.randomUUID(), "매체", PartOfSpeech.NOUN)
                            )
                    ).build());

    @Test
    @DisplayName("[QuizService] Generate OX quiz test")
    void generateOxQuizTest() {
        UUID userId = UUID.randomUUID();

        List<List<DefinitionDto>> defDtos = wordsWithDefs.stream().map(WordWithDefinitions::getDefinitionDtos).toList();

        //given
        given(wordService.getWordsByVocabularyId(any(User.class), any(UUID.class)))
                .willReturn(wordsWithDefs.stream().map(WordWithDefinitions::getWordDto).toList());
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        for (WordWithDefinitions w : wordsWithDefs) {
            given(definitionService.getDefinitionsByWordId(any(User.class), eq(w.getWordDto().getId())))
                    .willReturn(w.getDefinitionDtos());
        }

        given(vocabularyService.getVocabularyById(any(User.class), any(UUID.class)))
                .willReturn(
                        VocabularyDto.builder()
                                .id(UUID.randomUUID())
                                .userId(userId)
                                .build()
                );

        int epoch = 10;
        int assertCnt = 0;

        for (int i = 0; i < epoch; i++) {
            //when
            List<QuizDto> result = quizService.getOXQuizzes(testUser, UUID.randomUUID());

            //then
            int matchCnt = 0;
            for (QuizDto question : result) {
                for (WordWithDefinitions wordWithDefinitions : wordsWithDefs) {
                    if (question.getStem().equals(wordWithDefinitions.getWordDto().getExpression())
                            && question.getOptions().getFirst().equals(wordWithDefinitions.getDefinitionDtos().getFirst().getDefinition())) {
                        matchCnt++;
                        break;
                    }
                }
            }

            if (matchCnt < (wordsWithDefs.size() / 2) + 1) {
                assertCnt++;
            }
        }

        assertEquals(epoch, assertCnt);
    }

    @Test
    @DisplayName("[QuizService] Rate ox quiz test")
    void rateOxQuizTest() {
        //given
        List<String> tokens = new ArrayList<>();
        // quiz:{userId}:{wordId}:{quizId}
        for (WordWithDefinitions wordsWithDef : wordsWithDefs) {
            String token = "quiz"
                    + ":" + testUser.getId()
                    + ":" + wordsWithDef.getWordDto().getId();
            tokens.add(cryptoService.encrypt(token));
        }
        List<String> correctAnswers = new ArrayList<>();
        for (WordWithDefinitions wordWithDefinitions : wordsWithDefs) {
            correctAnswers.add(wordWithDefinitions.getDefinitionDtos().getFirst().getDefinition());
        }
        List<QuizRateRequest> quizRateRequests = new ArrayList<>();
        for (int i = 0; i < wordsWithDefs.size(); i++) {
            QuizRateRequest quizRateRequest = QuizRateRequest.builder()
                    .answer(correctAnswers.get(i))
                    .token(tokens.get(i))
                    .build();

            quizRateRequests.add(quizRateRequest);
        }

        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        given(redisTemplate.hasKey(anyString()))
                .willReturn(true);
        given(valueOperations.get(anyString()))
                .willReturn(correctAnswers.get(0), correctAnswers.get(1), correctAnswers.get(2), correctAnswers.get(3), correctAnswers.get(4));

        for (int i = 0; i < wordsWithDefs.size(); i++) {
            //when
            QuizRateResponse quizRateResponse = quizService.rateQuiz(testUser, quizRateRequests.get(i));

            //then
            assertTrue(quizRateResponse.getIsCorrect());
        }
    }
}
