package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dictionary.model.WordWithDefs;
import com.kimtaeyang.mobidic.quiz.dto.QuizDto;
import com.kimtaeyang.mobidic.quiz.dto.QuizStatisticDto;
import com.kimtaeyang.mobidic.quiz.service.CryptoService;
import com.kimtaeyang.mobidic.quiz.service.QuizService;
import com.kimtaeyang.mobidic.statistic.service.StatisticService;
import com.kimtaeyang.mobidic.dictionary.type.PartOfSpeech;
import com.kimtaeyang.mobidic.dictionary.dto.DefinitionDto;
import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDto;
import com.kimtaeyang.mobidic.dictionary.dto.WordDto;
import com.kimtaeyang.mobidic.dictionary.service.DefinitionService;
import com.kimtaeyang.mobidic.dictionary.service.VocabularyService;
import com.kimtaeyang.mobidic.dictionary.service.WordService;
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
@ContextConfiguration(classes = {QuizService.class, QuizServiceTest.TestConfig.class})
@ActiveProfiles("dev")
public class QuizServiceTest {
    @Autowired
    private QuizService quizService;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private StatisticService statisticService;

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
                    .definitionDtos(List.of(
                                    new DefinitionDto(UUID.randomUUID(), UUID.randomUUID(), "사과", PartOfSpeech.NOUN)
                            )
                    ).build(),
            WordWithDefs.builder()
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
            WordWithDefs.builder()
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
            WordWithDefs.builder()
                    .wordDto(
                            WordDto.builder()
                                    .id(UUID.randomUUID())
                                    .expression("Idiot")
                                    .build()
                    )
                    .definitionDtos(List.of(
                                    new DefinitionDto(UUID.randomUUID(), UUID.randomUUID(), "바보", PartOfSpeech.NOUN)
                            )
                    ).build(), WordWithDefs.builder()
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
        UUID memberId = UUID.randomUUID();

        List<List<DefinitionDto>> defDtos = wordsWithDefs.stream().map(WordWithDefs::getDefinitionDtos).toList();

        //given
        given(wordService.getWordsByVocabId(any(UUID.class)))
                .willReturn(wordsWithDefs.stream().map(WordWithDefs::getWordDto).toList());
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        for (WordWithDefs w : wordsWithDefs) {
            given(definitionService.getDefinitionsByWordId(eq(w.getWordDto().getId())))
                    .willReturn(w.getDefinitionDtos());
        }

        given(vocabularyService.getVocabById(any(UUID.class)))
                .willReturn(
                        VocabularyDto.builder()
                                .id(UUID.randomUUID())
                                .memberId(memberId)
                                .build()
                );

        int epoch = 10;
        int assertCnt = 0;

        for (int i = 0; i < epoch; i++) {
            //when
            List<QuizDto> result = quizService.getOXQuizzes(UUID.randomUUID());

            //then
            int matchCnt = 0;
            for (QuizDto question : result) {
                for (WordWithDefs wordWithDefs : wordsWithDefs) {
                    if (question.getStem().equals(wordWithDefs.getWordDto().getExpression())
                            && question.getOptions().getFirst().equals(wordWithDefs.getDefinitionDtos().getFirst().getDefinition())) {
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
            correctAnswers.add(wordWithDefs.getDefinitionDtos().getFirst().getDefinition());
        }
        List<QuizStatisticDto.Request> requests = new ArrayList<>();
        for (int i = 0; i < wordsWithDefs.size(); i++) {
            QuizStatisticDto.Request request = QuizStatisticDto.Request.builder()
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
            QuizStatisticDto.Response response = quizService.rateQuestion(memberId, requests.get(i));

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
        public DefinitionService defService() {
            return Mockito.mock(DefinitionService.class);
        }

        @Bean
        public StatisticService rateService() {
            return Mockito.mock(StatisticService.class);
        }

        @Bean
        public VocabularyService vocabService() {
            return Mockito.mock(VocabularyService.class);
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
