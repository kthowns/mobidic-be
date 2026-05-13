package com.kthowns.mobidic.service;

import com.kthowns.mobidic.config.ServiceTestConfig;
import com.kthowns.mobidic.dictionary.dto.DefinitionDto;
import com.kthowns.mobidic.dictionary.dto.WordDetail;
import com.kthowns.mobidic.dictionary.service.DefinitionService;
import com.kthowns.mobidic.dictionary.service.VocabularyService;
import com.kthowns.mobidic.dictionary.service.WordService;
import com.kthowns.mobidic.dictionary.type.PartOfSpeech;
import com.kthowns.mobidic.quiz.dto.QuizDto;
import com.kthowns.mobidic.quiz.dto.QuizRateRequest;
import com.kthowns.mobidic.quiz.dto.QuizRateResponse;
import com.kthowns.mobidic.quiz.service.CryptoService;
import com.kthowns.mobidic.quiz.service.QuizService;
import com.kthowns.mobidic.user.entity.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    List<WordDetail> wordDetails = List.of(
            WordDetail.builder()
                    .id(UUID.randomUUID())
                    .expression("Apple")
                    .definitions(List.of(
                                    new DefinitionDto(UUID.randomUUID(), "사과", PartOfSpeech.NOUN)
                            )
                    ).build(),
            WordDetail.builder()
                    .id(UUID.randomUUID())
                    .expression("Hello")
                    .definitions(List.of(
                                    new DefinitionDto(UUID.randomUUID(), "안녕", PartOfSpeech.INTERJECTION)
                            )
                    ).build(),
            WordDetail.builder()
                    .id(UUID.randomUUID())
                    .expression("Run")
                    .definitions(List.of(
                                    new DefinitionDto(UUID.randomUUID(), "뛰다", PartOfSpeech.VERB)
                            )
                    ).build(),
            WordDetail.builder()
                    .id(UUID.randomUUID())
                    .expression("Idiot")
                    .definitions(List.of(
                                    new DefinitionDto(UUID.randomUUID(), "바보", PartOfSpeech.NOUN)
                            )
                    ).build(), WordDetail.builder()
                    .id(UUID.randomUUID())
                    .expression("Media")
                    .definitions(List.of(
                                    new DefinitionDto(UUID.randomUUID(), "매체", PartOfSpeech.NOUN)
                            )
                    ).build());

    @Test
    @DisplayName("[QuizService] Generate OX quiz test")
    void generateOxQuizTest() {
        //given
        given(wordService.getWordDetailsByVocabularyId(any(User.class), any(UUID.class)))
                .willReturn(wordDetails);
        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);

        int epoch = 10;
        int assertCnt = 0;

        for (int i = 0; i < epoch; i++) {
            //when
            List<QuizDto> result = quizService.getOXQuizzes(testUser, UUID.randomUUID());

            //then
            int matchCnt = 0;
            for (QuizDto question : result) {
                for (WordDetail wordDetails : wordDetails) {
                    if (question.getStem().equals(wordDetails.expression())
                            && question.getOptions().getFirst().equals(wordDetails.definitions().getFirst().getMeaning())) {
                        matchCnt++;
                        break;
                    }
                }
            }

            if (matchCnt < (wordDetails.size() / 2) + 1) {
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
        for (WordDetail wordDetails : wordDetails) {
            String token = "quiz"
                    + ":" + testUser.getId()
                    + ":" + wordDetails.id();
            tokens.add(cryptoService.encrypt(token));
        }
        List<String> correctAnswers = new ArrayList<>();
        for (WordDetail wordDetails : wordDetails) {
            correctAnswers.add(wordDetails.definitions().getFirst().getMeaning());
        }
        List<QuizRateRequest> quizRateRequests = new ArrayList<>();
        for (int i = 0; i < wordDetails.size(); i++) {
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

        for (int i = 0; i < wordDetails.size(); i++) {
            //when
            QuizRateResponse quizRateResponse = quizService.rateQuiz(testUser, quizRateRequests.get(i));

            //then
            assertTrue(quizRateResponse.getIsCorrect());
        }
    }
}
