package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.api.vocabulary.dto.request.AddVocabularyRequestDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 단어장 관련 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class VocabularyIntegrationTest {

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
    private WordStatisticJpaRepository wordStatisticJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private jakarta.persistence.EntityManager em;

    private UserJpaEntity testUser;
    private String userToken;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();

        testUser = userJpaRepository.save(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .role(UserRole.USER)
                .build());

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("단어장 추가 성공")
    void addVocabularySuccess() throws Exception {
        // Given
        AddVocabularyRequestDto request = AddVocabularyRequestDto.builder()
                .title("테스트 단어장")
                .description("설명")
                .build();

        // When
        mockMvc.perform(post("/api/vocabularies")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());

        // Then
        VocabularyJpaEntity savedVocab = vocabularyJpaRepository.findAll().get(0);
        assertThat(savedVocab.getTitle()).isEqualTo("테스트 단어장");
        assertThat(savedVocab.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("단어장 추가 실패 - 중복된 제목")
    void addVocabularyFailDuplicated() throws Exception {
        // Given
        vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("중복 제목")
                .build());

        AddVocabularyRequestDto request = AddVocabularyRequestDto.builder()
                .title("중복 제목")
                .build();

        em.flush();
        em.clear();

        // When
        mockMvc.perform(post("/api/vocabularies")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.DUPLICATED_TITLE.getMessage()));
    }

    @Test
    @DisplayName("사용자별 단어장 목록 조회 성공")
    void getVocabulariesSuccess() throws Exception {
        // Given
        VocabularyJpaEntity vocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("단어장1")
                .wordCount(3L)
                .build());

        // 단어 3개 및 통계 주입 (학습률 1/3, 정확도 50% 유도)
        WordJpaEntity w1 = wordJpaRepository.save(WordJpaEntity.builder().vocabulary(vocab).expression("w1").build());
        WordJpaEntity w2 = wordJpaRepository.save(WordJpaEntity.builder().vocabulary(vocab).expression("w2").build());
        WordJpaEntity w3 = wordJpaRepository.save(WordJpaEntity.builder().vocabulary(vocab).expression("w3").build());

        wordStatisticJpaRepository.save(WordStatisticJpaEntity.builder().word(w1).isLearned(true).build()); // 학습됨
        wordStatisticJpaRepository.save(WordStatisticJpaEntity.builder().word(w2).correctCount(1).incorrectCount(1).build()); // 정확도 50%
        wordStatisticJpaRepository.save(WordStatisticJpaEntity.builder().word(w3).build());

        em.flush();
        em.clear();

        // When
        mockMvc.perform(get("/api/vocabularies")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].vocabulary.title").value("단어장1"))
                .andExpect(jsonPath("$.data[0].learningRate").value(0.33333))
                .andExpect(jsonPath("$.data[0].accuracy").value(0.16666666666666666));
    }

    @Test
    @DisplayName("단어장 상세 조회 성공")
    void getVocabularyDetailsSuccess() throws Exception {
        // Given
        VocabularyJpaEntity vocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("상세 조회용")
                .wordCount(10L)
                .build());
        em.flush();
        em.clear();

        // When
        mockMvc.perform(get("/api/vocabularies/" + vocab.getId())
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.vocabulary.id").value(vocab.getId().toString()))
                .andExpect(jsonPath("$.data.vocabulary.title").value("상세 조회용"))
                .andExpect(jsonPath("$.data.vocabulary.wordCount").value(10));
    }

    @Test
    @DisplayName("단어장 수정 성공")
    void updateVocabularySuccess() throws Exception {
        // Given
        VocabularyJpaEntity vocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("기존 제목")
                .build());

        AddVocabularyRequestDto request = AddVocabularyRequestDto.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .build();

        em.flush();
        em.clear();

        // When
        mockMvc.perform(patch("/api/vocabularies/" + vocab.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();
        VocabularyJpaEntity updatedVocab = vocabularyJpaRepository.findById(vocab.getId()).orElseThrow();
        assertThat(updatedVocab.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedVocab.getDescription()).isEqualTo("수정된 설명");
    }

    @Test
    @DisplayName("단어장 삭제 성공")
    void deleteVocabularySuccess() throws Exception {
        // Given
        VocabularyJpaEntity vocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("삭제할 단어장")
                .build());

        em.flush();
        em.clear();

        // When
        mockMvc.perform(delete("/api/vocabularies/" + vocab.getId())
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();
        assertThat(vocabularyJpaRepository.findById(vocab.getId())).isEmpty();
    }

    @Test
    @DisplayName("단어장 상세 조회 실패 - 존재하지 않는 단어장")
    void getVocabularyDetailsFailNoVocab() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();

        // When
        mockMvc.perform(get("/api/vocabularies/" + randomId)
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("단어장 수정 실패 - 존재하지 않는 단어장")
    void updateVocabularyFailNoVocab() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();
        AddVocabularyRequestDto request = AddVocabularyRequestDto.builder()
                .title("수정 시도")
                .build();

        // When
        mockMvc.perform(patch("/api/vocabularies/" + randomId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("단어장 삭제 실패 - 존재하지 않는 단어장")
    void deleteVocabularyFailNoVocab() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();

        // When
        mockMvc.perform(delete("/api/vocabularies/" + randomId)
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When
        mockMvc.perform(get("/api/vocabularies"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When
        mockMvc.perform(get("/api/vocabularies")
                        .header("Authorization", "Bearer invalid-token"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
