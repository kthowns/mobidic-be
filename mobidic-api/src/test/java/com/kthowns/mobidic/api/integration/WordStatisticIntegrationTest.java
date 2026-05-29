package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.user.model.UserRole;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 단어 통계 관련 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class WordStatisticIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    private WordStatisticJpaRepository wordStatisticJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private jakarta.persistence.EntityManager em;

    private UserJpaEntity testUser;
    private String userToken;
    private VocabularyJpaEntity testVocab;
    private WordJpaEntity testWord;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();

        testUser = userJpaRepository.saveAndFlush(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .role(UserRole.USER)
                .build());

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());

        testVocab = vocabularyJpaRepository.saveAndFlush(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("통계 단어장")
                .build());

        testWord = wordJpaRepository.saveAndFlush(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("apple")
                .build());

        wordStatisticJpaRepository.saveAndFlush(WordStatisticJpaEntity.builder()
                .word(testWord)
                .correctCount(5)
                .incorrectCount(5)
                .isLearned(false)
                .build());

        em.clear();
    }

    @Test
    @DisplayName("단어 통계 조회 성공")
    void getWordStatisticSuccess() throws Exception {
        // When
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/statistic")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.wordId").value(testWord.getId().toString()))
                .andExpect(jsonPath("$.data.correctCount").value(5))
                .andExpect(jsonPath("$.data.incorrectCount").value(5))
                .andExpect(jsonPath("$.data.isLearned").value(false));
    }

    @Test
    @DisplayName("단어장 학습률 조회 성공")
    void getVocabLearningRateSuccess() throws Exception {
        // Given: 2번째 단어 추가 및 학습 완료 처리
        WordJpaEntity word2 = wordJpaRepository.saveAndFlush(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("banana")
                .build());
        wordStatisticJpaRepository.saveAndFlush(WordStatisticJpaEntity.builder()
                .word(word2)
                .isLearned(true)
                .build());
        em.clear();

        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/learning-rate")
                        .header("Authorization", "Bearer " + userToken))
                // Then: 2개 중 1개 학습 완료이므로 0.5
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0.5));
    }

    @Test
    @DisplayName("단어장 평균 정확도 조회 성공")
    void getAvgAccuracyByVocabSuccess() throws Exception {
        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/accuracy")
                        .header("Authorization", "Bearer " + userToken))
                // Then: 5:5 비율이므로 0.5 (accuracy는 %가 아닌 0~1 사이 값)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0.5));
    }

    @Test
    @DisplayName("단어 학습 상태 토글 성공")
    void toggleLearnedStatusSuccess() throws Exception {
        // 1. When: 토글 API 호출 (false -> true)
        mockMvc.perform(patch("/api/words/" + testWord.getId() + "/toggle-learned")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // 2. Then (DB Verify): DB 직접 확인
        WordStatisticJpaEntity stat = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        assertThat(stat.isLearned()).isTrue();

        // 3. When: 다시 토글 (true -> false)
        mockMvc.perform(patch("/api/words/" + testWord.getId() + "/toggle-learned")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // 4. Then (DB Verify): 다시 false 확인
        stat = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        assertThat(stat.isLearned()).isFalse();
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/statistic"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/statistic")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
