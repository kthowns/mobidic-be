package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.definition.dto.request.AddDefinitionRequestDto;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.definition.model.PartOfSpeech;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.storage.definition.jpaentity.DefinitionJpaEntity;
import com.kthowns.mobidic.storage.definition.jparepository.DefinitionJpaRepository;
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
 * 단어 정의 관련 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DefinitionIntegrationTest {

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
    private PasswordEncoder passwordEncoder;

    private UserJpaEntity testUser;
    private String userToken;
    private WordJpaEntity testWord;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();

        // 테스트 기초 데이터 생성 (사용자 -> 단어장 -> 단어)
        testUser = userJpaRepository.save(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .role(UserRole.USER)
                .build());

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());

        VocabularyJpaEntity vocabulary = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(testUser)
                .title("테스트 단어장")
                .description("설명")
                .build());

        testWord = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(vocabulary)
                .expression("apple")
                .build());
    }

    @Test
    @DisplayName("정의 추가 성공")
    void addDefinitionSuccess() throws Exception {
        // Given
        AddDefinitionRequestDto request = AddDefinitionRequestDto.builder()
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build();

        // When
        mockMvc.perform(post("/api/words/" + testWord.getId() + "/definition")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then (1): 성공 응답 확인 (data는 null임)
                .andExpect(status().isOk());

        // Then (2): 실제 DB에 데이터가 정상적으로 저장되었는지 확인
        DefinitionJpaEntity savedDef = definitionJpaRepository.findAll().get(0);
        assertThat(savedDef.getMeaning()).isEqualTo("사과");
        assertThat(savedDef.getPart()).isEqualTo(PartOfSpeech.NOUN);
        assertThat(savedDef.getWord().getId()).isEqualTo(testWord.getId());
    }

    @Test
    @DisplayName("정의 추가 실패 - 중복된 정의")
    void addDefinitionFailDuplicated() throws Exception {
        // Given
        definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(testWord)
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build());

        AddDefinitionRequestDto request = AddDefinitionRequestDto.builder()
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build();

        // When
        mockMvc.perform(post("/api/words/" + testWord.getId() + "/definition")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.DUPLICATED_DEFINITION.getMessage()));
    }

    @Test
    @DisplayName("정의 추가 실패 - 존재하지 않는 단어")
    void addDefinitionFailNoWord() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();
        AddDefinitionRequestDto request = AddDefinitionRequestDto.builder()
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build();

        // When
        mockMvc.perform(post("/api/words/" + randomId + "/definition")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_WORD.getMessage()));
    }

    @Test
    @DisplayName("정의 조회 성공")
    void getDefinitionsSuccess() throws Exception {
        // Given
        definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(testWord)
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build());

        // When
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/definitions")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].meaning").value("사과"))
                .andExpect(jsonPath("$.data[0].part").value("NOUN"));
    }

    @Test
    @DisplayName("정의 수정 성공")
    void updateDefinitionSuccess() throws Exception {
        // Given
        DefinitionJpaEntity definition = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(testWord)
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build());

        AddDefinitionRequestDto updateRequest = AddDefinitionRequestDto.builder()
                .meaning("꿀사과")
                .part(PartOfSpeech.NOUN)
                .build();

        // When
        mockMvc.perform(patch("/api/definitions/" + definition.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                // Then (1): 성공 응답 확인 (data는 null임)
                .andExpect(status().isOk());

        // Then (2): 실제 DB에 데이터가 수정되었는지 확인
        DefinitionJpaEntity updatedDef = definitionJpaRepository.findById(definition.getId()).orElseThrow();
        assertThat(updatedDef.getMeaning()).isEqualTo("꿀사과");
    }

    @Test
    @DisplayName("정의 삭제 성공")
    void deleteDefinitionSuccess() throws Exception {
        // Given
        DefinitionJpaEntity definition = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(testWord)
                .meaning("사과")
                .part(PartOfSpeech.NOUN)
                .build());

        // When
        mockMvc.perform(delete("/api/definitions/" + definition.getId())
                        .header("Authorization", "Bearer " + userToken))
                // Then (1): 성공 응답 확인 (data는 null임)
                .andExpect(status().isOk());

        // Then (2): 실제 DB에서 데이터가 삭제되었는지 확인
        assertThat(definitionJpaRepository.findById(definition.getId())).isEmpty();
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/definitions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/words/" + testWord.getId() + "/definitions")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
