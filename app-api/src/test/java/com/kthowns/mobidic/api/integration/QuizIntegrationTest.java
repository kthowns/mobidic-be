package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.quiz.dto.request.QuizRateRequest;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.definition.model.Definition;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.quiz.model.QuizInfo;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.security.util.JwtProvider;
import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import com.kthowns.mobidic.storage.definition.jparepository.DefinitionJpaRepository;
import com.kthowns.mobidic.storage.statistic.jpaentity.WordStatisticJpaEntity;
import com.kthowns.mobidic.storage.statistic.jparepository.WordStatisticJpaRepository;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import com.kthowns.mobidic.storage.vocabulary.jpaentity.VocabularyJpaEntity;
import com.kthowns.mobidic.storage.vocabulary.jparepository.VocabularyJpaRepository;
import com.kthowns.mobidic.storage.word.jpaentity.WordJpaEntity;
import com.kthowns.mobidic.storage.word.jparepository.WordJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 퀴즈 관련 통합 테스트
 * OX 퀴즈 및 빈칸 퀴즈의 생성과 채점 로직을 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuizIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private VocabularyJpaRepository vocabularyJpaRepository;

    @Autowired
    private WordJpaRepository wordJpaRepository;

    @Autowired
    private DefinitionJpaRepository definitionJpaRepository;

    @Autowired
    private WordStatisticJpaRepository wordStatisticJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UserJpaEntity testUser;
    private String userToken;
    private VocabularyJpaEntity testVocab;
    private Map<String, String> wordToMeaning;

    @BeforeEach
    void setup() {
        transactionTemplate.execute(status -> {
            testUser = userJpaRepository.save(UserJpaEntity.createFromModel(
                    User.create("test@test.com", "test", passwordEncoder.encode("password123!"), UserRole.USER)));

            testVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.createFromModel(
                    Vocabulary.create(testUser.getId(), "퀴즈 단어장", null, 0L)));

            String[] expressions = {"apple", "banana", "car", "dog", "elephant"};
            String[] meanings = {"사과", "바나나", "자동차", "개", "코끼리"};
            wordToMeaning = Map.of(
                    "apple", "사과",
                    "banana", "바나나",
                    "car", "자동차",
                    "dog", "개",
                    "elephant", "코끼리"
            );

            for (int i = 0; i < expressions.length; i++) {
                WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.createFromModel(
                        Word.create(testVocab.getId(), expressions[i]), testVocab));

                definitionJpaRepository.save(DefinitionJpaEntity.createFromModel(
                        Definition.create(word.getId(), meanings[i], PartOfSpeech.NOUN), word));

                wordStatisticJpaRepository.save(WordStatisticJpaEntity.createFromModel(
                        WordStatistic.create(word.getId())));
            }
            return null;
        });

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.execute(status -> {
            definitionJpaRepository.deleteAllInBatch();
            wordStatisticJpaRepository.deleteAllInBatch();
            wordJpaRepository.deleteAllInBatch();
            vocabularyJpaRepository.deleteAllInBatch();
            userJpaRepository.deleteAllInBatch();
            return null;
        });
    }

    @Test
    @DisplayName("OX 퀴즈 생성 및 채점 성공")
    void oxQuizCreateAndRateSuccess() throws Exception {
        // When
        MvcResult generateResult = mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/quizzes/ox")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andReturn();

        // Given
        List<QuizInfo> quizzes = objectMapper.readValue(
                objectMapper.readTree(generateResult.getResponse().getContentAsString()).path("data").toString(),
                new TypeReference<List<QuizInfo>>() {
                }
        );

        assertThat(quizzes).hasSize(5);

        QuizInfo quiz = quizzes.getFirst();
        String answer = wordToMeaning.get(quiz.stem()).equals(quiz.options().getFirst()) ? "1" : "0";

        QuizRateRequest rateRequest = QuizRateRequest.builder()
                .token(quiz.token())
                .answer(answer)
                .build();

        // When
        mockMvc.perform(post("/api/quizzes/rate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isCorrect").value(true));

        // Then (데이터 반영 확인은 별도 트랜잭션 없이 findAll로 확인 가능)
        WordJpaEntity word = wordJpaRepository.findAll().stream()
                .filter(w -> w.getExpression().equals(quiz.stem())).findFirst().orElseThrow();
        WordStatisticJpaEntity statistic = wordStatisticJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(statistic.getCorrectCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("빈칸 퀴즈 생성 및 채점 성공")
    void blankQuizCreateAndRateSuccess() throws Exception {
        // When
        MvcResult generateResult = mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/quizzes/blank")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andReturn();

        // Given
        List<QuizInfo> quizzes = objectMapper.readValue(
                objectMapper.readTree(generateResult.getResponse().getContentAsString()).path("data").toString(),
                new TypeReference<>() {
                }
        );

        assertThat(quizzes).hasSize(5);
        assertThat(quizzes.getFirst().stem()).contains("_");

        QuizInfo quiz = quizzes.getFirst();
        String correctAnswer = wordToMeaning.keySet().stream()
                .filter(w -> isMatchPattern(w, quiz.stem())).findFirst().orElseThrow();

        QuizRateRequest rateRequest = QuizRateRequest.builder()
                .token(quiz.token())
                .answer(correctAnswer)
                .build();

        // When
        mockMvc.perform(post("/api/quizzes/rate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isCorrect").value(true));

        // Then
        WordJpaEntity word = wordJpaRepository.findAll().stream()
                .filter(w -> w.getExpression().equals(correctAnswer)).findFirst().orElseThrow();
        WordStatisticJpaEntity statistic = wordStatisticJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(statistic.getCorrectCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("퀴즈 생성 무작위성 검증")
    void quizGenerationRandomness() throws Exception {
        // Given
        List<QuizInfo> baseQuizzes = getQuizzes();
        assertThat(baseQuizzes).hasSize(5);

        boolean existsDifferentOrder = false;

        // When
        for (int i = 0; i < 20; i++) {
            List<QuizInfo> currentQuizzes = getQuizzes();

            if (!isExactlySameList(baseQuizzes, currentQuizzes)) {
                existsDifferentOrder = true;
                break;
            }
        }

        // Then
        assertThat(existsDifferentOrder).isTrue();
    }

    // 헬퍼 메서드: 두 리스트의 순서와 내용이 완벽히 일치하는지 확인
    private boolean isExactlySameList(List<QuizInfo> list1, List<QuizInfo> list2) {
        if (list1.size() != list2.size()) return false;
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).stem().equals(list2.get(i).stem())) {
                return false;
            }
        }
        return true;
    }

    // HTTP 요청 중복 코드 제거용 헬퍼
    private List<QuizInfo> getQuizzes() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/quizzes/ox")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(
                objectMapper.readTree(result.getResponse().getContentAsString()).path("data").toString(),
                new TypeReference<>() {
                }
        );
    }

    @Test
    @DisplayName("퀴즈 생성 실패 - 존재하지 않는 단어장")
    void quizCreateFailNoVocab() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();

        // When
        mockMvc.perform(get("/api/vocabularies/" + randomId + "/quizzes/ox")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_VOCAB.getMessage()));
    }

    private boolean isMatchPattern(String word, String pattern) {
        if (word.length() != pattern.length()) return false;
        for (int i = 0; i < word.length(); i++) {
            if (pattern.charAt(i) != '_' && pattern.charAt(i) != word.charAt(i)) return false;
        }
        return true;
    }
}
