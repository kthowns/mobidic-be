package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.quiz.dto.request.QuizRateRequest;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.quiz.model.QuizInfo;
import com.kthowns.mobidic.domain.user.model.UserRole;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuizIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DatabaseCleaner databaseCleaner;

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
    private jakarta.persistence.EntityManager em;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UserJpaEntity testUser;
    private String userToken;
    private VocabularyJpaEntity testVocab;
    private Map<String, String> wordToMeaning;

    @BeforeAll
    void cleanAndSetup() {
        transactionTemplate.execute(status -> {
            databaseCleaner.execute();

            // 1. 테스트 사용자 및 인증 토큰 생성
            testUser = userJpaRepository.save(UserJpaEntity.builder()
                    .email("test@test.com")
                    .nickname("test")
                    .password(passwordEncoder.encode("password123!"))
                    .role(UserRole.USER)
                    .build());

            userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());

            // 2. 테스트 단어장 생성
            testVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                    .user(testUser)
                    .title("퀴즈 단어장")
                    .build());

            // 3. 퀴즈 생성을 위한 단어, 정의, 통계 데이터 주입
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
                WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                        .vocabulary(testVocab)
                        .expression(expressions[i])
                        .build());

                definitionJpaRepository.save(DefinitionJpaEntity.builder()
                        .word(word)
                        .meaning(meanings[i])
                        .part(PartOfSpeech.NOUN)
                        .build());

                wordStatisticJpaRepository.save(WordStatisticJpaEntity.builder()
                        .word(word)
                        .build());
            }
            return null;
        });

        em.clear(); // DB 주입 데이터 반영 보장
    }

    @Test
    @DisplayName("OX 퀴즈 생성 및 채점 성공")
    void oxQuizCreateAndRateSuccess() throws Exception {
        // When (Generate): OX 퀴즈 생성 API 호출
        MvcResult generateResult = mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/quizzes/ox")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        List<QuizInfo> quizzes = objectMapper.readValue(
                objectMapper.readTree(generateResult.getResponse().getContentAsString()).path("data").toString(),
                new TypeReference<List<QuizInfo>>() {}
        );

        // Then (Generate): 퀴즈 개수 확인
        assertThat(quizzes).hasSize(5);

        // When (Rate): 첫 번째 퀴즈 정답 제출
        QuizInfo quiz = quizzes.get(0);
        String answer = wordToMeaning.get(quiz.stem()).equals(quiz.options().get(0)) ? "1" : "0";

        QuizRateRequest rateRequest = QuizRateRequest.builder()
                .token(quiz.token())
                .answer(answer)
                .build();

        mockMvc.perform(post("/api/quizzes/rate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateRequest)))
                // Then (Rate): 채점 결과 및 DB 반영 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isCorrect").value(true));

        WordJpaEntity word = wordJpaRepository.findAll().stream()
                .filter(w -> w.getExpression().equals(quiz.stem())).findFirst().orElseThrow();
        WordStatisticJpaEntity statistic = wordStatisticJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(statistic.getCorrectCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("빈칸 퀴즈 생성 및 채점 성공")
    void blankQuizCreateAndRateSuccess() throws Exception {
        // When (Generate): 빈칸 퀴즈 생성 API 호출
        MvcResult generateResult = mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/quizzes/blank")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        List<QuizInfo> quizzes = objectMapper.readValue(
                objectMapper.readTree(generateResult.getResponse().getContentAsString()).path("data").toString(),
                new TypeReference<List<QuizInfo>>() {}
        );

        // Then (Generate): 퀴즈 개수 및 빈칸 포함 확인
        assertThat(quizzes).hasSize(5);
        assertThat(quizzes.get(0).stem()).contains("_");

        // When (Rate): 첫 번째 퀴즈 정답 제출
        QuizInfo quiz = quizzes.get(0);
        String correctAnswer = wordToMeaning.keySet().stream()
                .filter(w -> isMatchPattern(w, quiz.stem())).findFirst().orElseThrow();

        QuizRateRequest rateRequest = QuizRateRequest.builder()
                .token(quiz.token())
                .answer(correctAnswer)
                .build();

        mockMvc.perform(post("/api/quizzes/rate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateRequest)))
                // Then (Rate): 채점 결과 및 DB 반영 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isCorrect").value(true));

        WordJpaEntity word = wordJpaRepository.findAll().stream()
                .filter(w -> w.getExpression().equals(correctAnswer)).findFirst().orElseThrow();
        WordStatisticJpaEntity statistic = wordStatisticJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(statistic.getCorrectCount()).isEqualTo(1L);
    }
    @Test
    @DisplayName("퀴즈 생성 무작위성 검증")
    void quizGenerationRandomness() throws Exception {
        // 1. 기준이 되는 첫 번째 퀴즈 생성
        List<QuizInfo> baseQuizzes = getQuizzes();
        assertThat(baseQuizzes).hasSize(5);

        boolean existsDifferentOrder = false;

        // 2. 20번 반복 호출하면서 단 한 번이라도 순서나 구성이 달라지는지 확인
        for (int i = 0; i < 20; i++) {
            List<QuizInfo> currentQuizzes = getQuizzes();

            if (!isExactlySameList(baseQuizzes, currentQuizzes)) {
                existsDifferentOrder = true;
                break; // 단 한 번이라도 다르면 무작위성이 존재하므로 성공!
            }
        }

        // 3. 20번 중 단 한 번은 무조건 달라야 함 (셔플이 안 돌면 무조건 실패)
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
                new TypeReference<List<QuizInfo>>() {}
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
