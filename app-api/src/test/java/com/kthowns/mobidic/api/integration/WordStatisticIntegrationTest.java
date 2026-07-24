package com.kthowns.mobidic.api.integration;

import com.kthowns.mobidic.security.util.JwtProvider;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.global.model.AuditTime;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.word.model.Word;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

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
    private TransactionTemplate transactionTemplate;

    private UserJpaEntity testUser;
    private String userToken;
    private VocabularyJpaEntity testVocab;
    private WordJpaEntity testWord;

    @BeforeEach
    void setUp() {
        transactionTemplate.execute(status -> {
            testUser = userJpaRepository.save(UserJpaEntity.createFromModel(
                    User.create("test@test.com", "test", passwordEncoder.encode("password123!"), UserRole.USER)));

            testVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.createFromModel(
                    Vocabulary.create(testUser.getId(), "통계 단어장", null, 0L)));

            testWord = wordJpaRepository.save(WordJpaEntity.createFromModel(
                    Word.create(testVocab.getId(), "apple"), testVocab));

            WordStatistic statModel = new WordStatistic(testWord.getId(), 5L, 5L, false, 0.5, 0.5, AuditTime.create());
            WordStatisticJpaEntity statEntity = WordStatisticJpaEntity.createFromModel(statModel);
            statEntity.updateFromModel(statModel);
            wordStatisticJpaRepository.save(statEntity);
            return null;
        });

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.execute(status -> {
            // 데이터 삭제 순서: 자식 테이블부터 부모 테이블 순으로 (FK 제약 조건 고려)
            wordStatisticJpaRepository.deleteAllInBatch();
            wordJpaRepository.deleteAllInBatch();
            vocabularyJpaRepository.deleteAllInBatch();
            userJpaRepository.deleteAllInBatch();
            return null;
        });
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
        transactionTemplate.execute(status -> {
            WordJpaEntity word2 = wordJpaRepository.save(WordJpaEntity.createFromModel(
                    Word.create(testVocab.getId(), "banana"), testVocab));
            WordStatistic statModel2 = new WordStatistic(word2.getId(), 0L, 0L, true, 0.5, 0.0, AuditTime.create());
            WordStatisticJpaEntity statEntity2 = WordStatisticJpaEntity.createFromModel(statModel2);
            statEntity2.updateFromModel(statModel2);
            wordStatisticJpaRepository.save(statEntity2);
            return null;
        });

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
        VocabularyJpaEntity emptyVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.createFromModel(
                Vocabulary.create(testUser.getId(), "빈 단어장", null, 0L)));

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
        WordStatisticJpaEntity stat = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        assertThat(stat.isLearned()).isTrue();

        // When
        mockMvc.perform(patch("/api/words/" + testWord.getId() + "/toggle-learned")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk());

        // Then
        WordStatisticJpaEntity statAfter = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        assertThat(statAfter.isLearned()).isFalse();
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
        WordStatisticJpaEntity stat = wordStatisticJpaRepository.findById(testWord.getId()).orElseThrow();
        boolean expectedState = (successCount.get() % 2 != 0);
        assertThat(stat.isLearned()).isEqualTo(expectedState);
        assertThat(successCount.get()).isGreaterThan(0); // 적어도 한 번은 성공해야 데드락이 아님
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
