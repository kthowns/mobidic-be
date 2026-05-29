package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.api.word.dto.request.AddWordRequestDto;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 단어 관련 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 💡 BeforeAll을 non-static으로 쓰기 위함
public class WordIntegrationTest {

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

    @BeforeAll
    void cleanAndSetup() {
        transactionTemplate.execute(status -> {
            databaseCleaner.execute();

            testUser = userJpaRepository.save(UserJpaEntity.builder()
                    .email("test@test.com")
                    .nickname("test")
                    .password(passwordEncoder.encode("password123!"))
                    .role(UserRole.USER)
                    .build());

            userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());

            testVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                    .user(testUser)
                    .title("테스트 단어장")
                    .build());
            return null;
        });
        em.clear();
    }

    @Test
    @DisplayName("단어 추가 성공")
    void addWordSuccess() throws Exception {
        // Given
        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("apple")
                .build();

        // When
        mockMvc.perform(post("/api/vocabularies/" + testVocab.getId() + "/word")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                // Then (1): 성공 응답 확인 (data는 null)
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        // Then (2): DB 직접 확인
        WordJpaEntity savedWord = wordJpaRepository.findAll().get(0);
        assertThat(savedWord.getExpression()).isEqualTo("apple");
        assertThat(savedWord.getVocabulary().getId()).isEqualTo(testVocab.getId());

        // Then (3): 통계 데이터 자동 생성 확인 (레포지토리 전체 조회로 확인)
        List<WordStatisticJpaEntity> allStats = wordStatisticJpaRepository.findAll();
        assertThat(allStats).anyMatch(stat -> stat.getWord().getId().equals(savedWord.getId()));
    }

    @Test
    @DisplayName("단어 추가 실패 - 중복된 단어")
    void addWordFailDuplicated() throws Exception {
        // Given: 이미 존재하는 단어
        wordJpaRepository.saveAndFlush(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("apple")
                .build());

        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("apple")
                .build();

        // When
        mockMvc.perform(post("/api/vocabularies/" + testVocab.getId() + "/word")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.DUPLICATED_WORD.getMessage()));
    }

    @Test
    @DisplayName("단어장별 단어 목록 조회 성공")
    void getWordsSuccess() throws Exception {
        // Given: 단어 1개와 정의 주입
        WordJpaEntity word = wordJpaRepository.saveAndFlush(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("apple")
                .build());

        definitionJpaRepository.saveAndFlush(DefinitionJpaEntity.builder()
                .word(word)
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build());

        wordStatisticJpaRepository.saveAndFlush(WordStatisticJpaEntity.builder()
                .word(word)
                .difficulty(0.5)
                .accuracy(0.0)
                .build());

        em.clear();

        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/words")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].expression").value("apple"))
                .andExpect(jsonPath("$.data[0].definitions[0].meaning").value("사과"))
                .andExpect(jsonPath("$.data[0].difficulty").value(0.5))
                .andExpect(jsonPath("$.data[0].accuracy").value(0.0));
    }

    @Test
    @DisplayName("단어 수정 성공")
    void updateWordSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.saveAndFlush(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("apple")
                .build());

        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("banana")
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then (1)
                .andExpect(status().isOk());

        // Then (2): DB 직접 확인
        WordJpaEntity updatedWord = wordJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(updatedWord.getExpression()).isEqualTo("banana");
    }

    @Test
    @DisplayName("단어 삭제 성공")
    void deleteWordSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.saveAndFlush(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("apple")
                .build());

        // When
        mockMvc.perform(delete("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken))
                // Then (1)
                .andExpect(status().isOk());

        // Then (2): DB 직접 확인
        assertThat(wordJpaRepository.findById(word.getId())).isEmpty();
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/words"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/words")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
