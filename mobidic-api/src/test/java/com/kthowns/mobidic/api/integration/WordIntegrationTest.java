package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.definition.dto.request.AddDefinitionRequestDto;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.api.word.dto.request.AddWordRequestDto;
import com.kthowns.mobidic.api.word.dto.request.UpdateWordRequestDto;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
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

    private UserJpaEntity testUser;
    private String userToken;
    private VocabularyJpaEntity testVocab;

    @BeforeEach
    void setup() {
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

        // 셋업 데이터가 다음 쿼리에 영향 주지 않도록만 정리
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("단어 및 정의 일괄 추가 성공")
    void addWordWithDefinitionsSuccess() throws Exception {
        // Given
        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("apple")
                .definitions(List.of(
                        new AddDefinitionRequestDto("사과", PartOfSpeech.NOUN),
                        new AddDefinitionRequestDto("사과하다", PartOfSpeech.VERB)
                ))
                .build();

        // When
        mockMvc.perform(post("/api/vocabularies/" + testVocab.getId() + "/word")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());

        // Then
        WordJpaEntity savedWord = wordJpaRepository.findAll().stream()
                .filter(w -> w.getExpression().equals("apple"))
                .findFirst().orElseThrow();

        List<DefinitionJpaEntity> definitions = definitionJpaRepository.findByWord_Id(savedWord.getId());
        assertThat(definitions).hasSize(2);
    }

    @Test
    @DisplayName("단어 추가 성공")
    void addWordSuccess() throws Exception {
        // Given
        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("banana")
                .build();

        // When
        mockMvc.perform(post("/api/vocabularies/" + testVocab.getId() + "/word")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());

        // Then
        assertThat(wordJpaRepository.findAll().stream()
                .anyMatch(w -> w.getExpression().equals("banana"))).isTrue();
    }

    @Test
    @DisplayName("단어 일괄 추가 원자성(Atomic) 검증 - 정의 중복 시 409 반환")
    void addWordWithDefinitionsRollback() throws Exception {
        // Given
        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("RollbackTestWord")
                .definitions(List.of(
                        new AddDefinitionRequestDto("중복정의", PartOfSpeech.NOUN),
                        new AddDefinitionRequestDto("중복정의", PartOfSpeech.NOUN)
                ))
                .build();

        // When
        mockMvc.perform(post("/api/vocabularies/" + testVocab.getId() + "/word")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("단어 추가 실패 - 중복된 단어")
    void addWordFailDuplicated() throws Exception {
        // Given
        wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("dog")
                .build());

        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("dog")
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
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("elephant")
                .build());

        definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word)
                .meaning("코끼리")
                .part(PartOfSpeech.NOUN)
                .build());

        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/words")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.expression == 'elephant')].definitions[0].meaning").value("코끼리"));
    }

    @Test
    @DisplayName("단어 수정 성공")
    void updateWordSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("flower")
                .build());

        UpdateWordRequestDto request = UpdateWordRequestDto.builder()
                .expression("garden")
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();

        WordJpaEntity updatedWord = wordJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(updatedWord.getExpression()).isEqualTo("garden");
    }

    @Test
    @DisplayName("단어 삭제 성공")
    void deleteWordSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("home")
                .build());

        // When
        mockMvc.perform(delete("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();

        assertThat(wordJpaRepository.findById(word.getId())).isEmpty();
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/words"))
                // Then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When
        mockMvc.perform(get("/api/vocabularies/" + testVocab.getId() + "/words")
                        .header("Authorization", "Bearer invalid-token"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
