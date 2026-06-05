package com.kthowns.mobidic.api.integration;

import com.kthowns.mobidic.api.security.jwt.JwtProvider;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
        testUser = userJpaRepository.save(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .role(UserRole.USER)
                .build());

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());

        testVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("통계 단어장")
                .build());

        testWord = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("apple")
                .build());

        wordStatisticJpaRepository.save(WordStatisticJpaEntity.builder()
                .word(testWord)
                .correctCount(5)
                .incorrectCount(5)
                .isLearned(false)
                .build());

        em.flush();
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
        // Given
        WordJpaEntity word2 = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("banana")
                .build());
        wordStatisticJpaRepository.save(WordStatisticJpaEntity.builder()
                .word(word2)
                .isLearned(true)
                .build());

        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/learning-rate")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0.5));
    }

    @Test
    @DisplayName("단어장 학습률 조회 성공 - 단어가 0개인 경우 0.0 반환")
    void getVocabLearningRateEmptyVocabSuccess() throws Exception {
        // Given
        VocabularyJpaEntity emptyVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("빈 단어장")
                .build());

        // When
        mockMvc.perform(get("/api/vocabularies/" + emptyVocab.getId() + "/learning-rate")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0.0));
    }

    @Test
    @DisplayName("단어장 평균 정확도 조회 성공")
    void getAvgAccuracyByVocabSuccess() throws Exception {
        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/accuracy")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0.5));
    }

    @Test
    @DisplayName("단어 학습 상태 토글 성공")
    void toggleLearnedStatusSuccess() throws Exception {
        // When
        mockMvc.perform(patch("/api/words/" + testWord.getId() + "/toggle-learned")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();
        WordStatisticJpaEntity stat = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        assertThat(stat.isLearned()).isTrue();

        // When
        mockMvc.perform(patch("/api/words/" + testWord.getId() + "/toggle-learned")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();
        stat = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        assertThat(stat.isLearned()).isFalse();
    }

    @Test
    @DisplayName("동시성 테스트 - 단어 학습 상태 동시 토글")
    void toggleLearnedStatusConcurrency() throws Exception {
        // Given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(patch("/api/words/" + testWord.getId() + "/toggle-learned")
                                    .header("Authorization", "Bearer " + userToken))
                            .andReturn();

                    if (result.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        em.flush();
        em.clear();

        WordStatisticJpaEntity stat = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        boolean expectedState = (successCount.get() % 2 != 0);
        assertThat(stat.isLearned()).isEqualTo(expectedState);
    }

    @Test
    @DisplayName("단어 통계 조회 실패 - 존재하지 않는 단어")
    void getWordStatisticFailNoWord() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();

        // When
        mockMvc.perform(get("/api/words/" + randomId + "/statistic")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_STAT.getMessage()));
    }

    @Test
    @DisplayName("단어장 학습률 조회 실패 - 존재하지 않는 단어장")
    void getVocabLearningRateFailNoVocab() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();

        // When
        mockMvc.perform(get("/api/vocabularies/" + randomId + "/learning-rate")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("단어장 평균 정확도 조회 실패 - 존재하지 않는 단어장")
    void getAvgAccuracyByVocabFailNoVocab() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();

        // When
        mockMvc.perform(get("/api/vocabularies/" + randomId + "/accuracy")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("단어 학습 상태 토글 실패 - 존재하지 않는 단어")
    void toggleLearnedStatusFailNoWord() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/words/" + randomId + "/toggle-learned")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_STAT.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/statistic"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/statistic")
                        .header("Authorization", "Bearer invalid-token"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
