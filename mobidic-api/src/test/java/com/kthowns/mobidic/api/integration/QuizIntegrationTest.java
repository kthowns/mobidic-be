package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.quiz.dto.request.QuizRateRequest;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
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
import org.springframework.transaction.annotation.Transactional;

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

    private UserJpaEntity testUser;
    private String userToken;
    private VocabularyJpaEntity testVocab;
    private Map<String, String> wordToMeaning;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();

        // 1. 테스트 사용자 및 인증 토큰 생성
        testUser = userJpaRepository.saveAndFlush(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .role(UserRole.USER)
                .build());

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());

        // 2. 테스트 단어장 생성
        testVocab = vocabularyJpaRepository.saveAndFlush(VocabularyJpaEntity.builder()
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
            WordJpaEntity word = wordJpaRepository.saveAndFlush(WordJpaEntity.builder()
                    .vocabulary(testVocab)
                    .expression(expressions[i])
                    .build());

            definitionJpaRepository.saveAndFlush(DefinitionJpaEntity.builder()
                    .word(word)
                    .meaning(meanings[i])
                    .part(PartOfSpeech.NOUN)
                    .build());

            wordStatisticJpaRepository.saveAndFlush(WordStatisticJpaEntity.builder()
                    .word(word)
                    .build());
        }

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

    private boolean isMatchPattern(String word, String pattern) {
        if (word.length() != pattern.length()) return false;
        for (int i = 0; i < word.length(); i++) {
            if (pattern.charAt(i) != '_' && pattern.charAt(i) != word.charAt(i)) return false;
        }
        return true;
    }
}
