package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.*;
import com.kimtaeyang.mobidic.model.WordWithDefs;
import com.kimtaeyang.mobidic.type.PartOfSpeech;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {QuestionService.class, QuestionServiceTest.TestConfig.class})
@ActiveProfiles("dev")
public class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private VocabService vocabService;

    @Autowired
    private DefService defService;

    @Autowired
    private RateService rateService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private WordService wordService;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    List<WordWithDefs> wordsWithDefs = List.of(
            WordWithDefs.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Apple")
                                    .build()
                    )
                    .defDtos(List.of(
                                    new DefDto(UUID.randomUUID(), UUID.randomUUID(), "사과", PartOfSpeech.NOUN)
                            )
                    ).build(),
            WordWithDefs.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Hello")
                                    .build()
                    )
                    .defDtos(List.of(
                                    new DefDto(UUID.randomUUID(), UUID.randomUUID(), "안녕", PartOfSpeech.INTERJECTION)
                            )
                    ).build(),
            WordWithDefs.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Run")
                                    .build()
                    )
                    .defDtos(List.of(
                                    new DefDto(UUID.randomUUID(), UUID.randomUUID(), "뛰다", PartOfSpeech.VERB)
                            )
                    ).build(),
            WordWithDefs.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Idiot")
                                    .build()
                    )
                    .defDtos(List.of(
                                    new DefDto(UUID.randomUUID(), UUID.randomUUID(), "바보", PartOfSpeech.NOUN)
                            )
                    ).build(), WordWithDefs.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Media")
                                    .build()
                    )
                    .defDtos(List.of(
                                    new DefDto(UUID.randomUUID(), UUID.randomUUID(), "매체", PartOfSpeech.NOUN)
                            )
                    ).build());

    @Test
    @DisplayName("[QuizService] Generate OX quiz test")
    void generateOxQuizTest() {
        UUID memberId = UUID.randomUUID();

        List<List<DefDto>> defDtos = wordsWithDefs.stream().map(WordWithDefs::getDefDtos).toList();

        //given
        given(wordService.getWordsByVocabId(any(UUID.class)))
                .willReturn(wordsWithDefs.stream().map(WordWithDefs::getWordDto).toList());
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        for (WordWithDefs w : wordsWithDefs) {
            given(defService.getDefsByWordId(eq(w.getWordDto().getId())))
                    .willReturn(w.getDefDtos());
        }

        given(vocabService.getVocabById(any(UUID.class)))
                .willReturn(
                        VocabDto.builder()
                                .id(UUID.randomUUID())
                                .memberId(memberId)
                                .build()
                );

        int epoch = 10;
        int assertCnt = 0;

        for (int i = 0; i < epoch; i++) {
            //when
            List<QuestionDto> result = questionService.getOxQuestions(UUID.randomUUID());

            //then
            int matchCnt = 0;
            for (QuestionDto question : result) {
                for (WordWithDefs wordWithDefs : wordsWithDefs) {
                    if (question.getStem().equals(wordWithDefs.getWordDto().getExpression())
                            && question.getOptions().getFirst().equals(wordWithDefs.getDefDtos().getFirst().getDefinition())) {
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
        UUID memberId = UUID.randomUUID();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < wordsWithDefs.size(); i++) {
            String token = "question"
                    + ":" + wordsWithDefs.get(i).getWordDto().getId()
                    + ":" + UUID.randomUUID();
            tokens.add(cryptoService.encrypt(token));
        }
        List<String> correctAnswers = new ArrayList<>();
        for (WordWithDefs wordWithDefs : wordsWithDefs) {
            correctAnswers.add(wordWithDefs.getDefDtos().getFirst().getDefinition());
        }
        List<QuestionRateDto.Request> requests = new ArrayList<>();
        for (int i = 0; i < wordsWithDefs.size(); i++) {
            QuestionRateDto.Request request = QuestionRateDto.Request.builder()
                    .answer(correctAnswers.get(i))
                    .token(tokens.get(i))
                    .build();

            requests.add(request);
        }

        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        given(redisTemplate.hasKey(anyString()))
                .willReturn(true);
        given(valueOperations.get(anyString()))
                .willReturn(correctAnswers.get(0), correctAnswers.get(1), correctAnswers.get(2), correctAnswers.get(3), correctAnswers.get(4));

        for (int i = 0; i < wordsWithDefs.size(); i++) {
            //when
            QuestionRateDto.Response response = questionService.rateQuestion(memberId, requests.get(i));

            //then
            assertTrue(response.getIsCorrect());
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CryptoService cryptoService() {
            return new CryptoService();
        }

        @Bean
        public WordService wordService() {
            return Mockito.mock(WordService.class);
        }

        @Bean
        public DefService defService() {
            return Mockito.mock(DefService.class);
        }

        @Bean
        public RateService rateService() {
            return Mockito.mock(RateService.class);
        }

        @Bean
        public VocabService vocabService() {
            return Mockito.mock(VocabService.class);
        }

        @Bean
        public RedisTemplate redisTemplate() {
            return Mockito.mock(RedisTemplate.class);
        }

        @Bean
        public ValueOperations valueOperations() {
            return Mockito.mock(ValueOperations.class);
        }
    }
}
