package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.definition.dto.request.AddDefinitionRequestDto;
import com.kthowns.mobidic.api.definition.dto.request.UpdateDefinitionRequestDto;
import com.kthowns.mobidic.api.security.util.JwtProvider;
import com.kthowns.mobidic.api.word.dto.request.AddWordRequestDto;
import com.kthowns.mobidic.api.word.dto.request.UpdateWordAndDefinitionsRequestDto;
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
import jakarta.persistence.EntityManager;
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
    private UserJpaRepository userJpaRepository;

    @Autowired
    private VocabularyJpaRepository vocabularyJpaRepository;

    @Autowired
    private WordJpaRepository wordJpaRepository;

    @Autowired
    private DefinitionJpaRepository definitionJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager em;

    private UserJpaEntity testUser;
    private String userToken;
    private VocabularyJpaEntity testVocab;

    @BeforeEach
    void setup() {
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

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("단어 및 정의 일괄 추가 성공")
    void addWordWithDefinitionsSuccess() throws Exception {
        AddWordRequestDto request = AddWordRequestDto.builder()
                .expression("apple")
                .definitions(List.of(
                        new AddDefinitionRequestDto("사과", PartOfSpeech.NOUN),
                        new AddDefinitionRequestDto("사과하다", PartOfSpeech.VERB)
                ))
                .build();

        mockMvc.perform(post("/api/vocabularies/" + testVocab.getId() + "/word")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        WordJpaEntity savedWord = wordJpaRepository.findAll().stream()
                .filter(w -> w.getExpression().equals("apple"))
                .findFirst().orElseThrow();

        List<DefinitionJpaEntity> definitions = definitionJpaRepository.findByWord_IdAndWord_Vocabulary_User_Id(savedWord.getId(), testUser.getId());
        assertThat(definitions).hasSize(2);
    }

    @Test
    @DisplayName("단어 수정 및 정의 동기화(추가/수정/삭제) 성공")
    void updateWordAndSyncDefinitionsSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("oldExpression")
                .build());

        // 기존 뜻 2개 등록 (하나는 수정용, 하나는 삭제용)
        DefinitionJpaEntity defToUpdate = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word).meaning("기존뜻1").part(PartOfSpeech.NOUN).build());
        DefinitionJpaEntity defToDelete = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word).meaning("기존뜻2").part(PartOfSpeech.NOUN).build());

        em.flush();
        em.clear();

        UpdateWordAndDefinitionsRequestDto request = UpdateWordAndDefinitionsRequestDto.builder()
                .expression("newExpression")
                .updatingDefinitions(List.of(
                        new UpdateDefinitionRequestDto(defToUpdate.getId(), "수정된뜻1", PartOfSpeech.VERB)
                ))
                .addingDefinitions(List.of(
                        new AddDefinitionRequestDto("새로운뜻1", PartOfSpeech.ADJECTIVE)
                ))
                .deletingDefinitions(List.of(defToDelete.getId()))
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());

        // Then (검증)
        WordJpaEntity updatedWord = wordJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(updatedWord.getExpression()).isEqualTo("newExpression");

        List<DefinitionJpaEntity> currentDefs = definitionJpaRepository.findByWord_IdAndWord_Vocabulary_User_Id(word.getId(), testUser.getId());
        assertThat(currentDefs).hasSize(2);
        assertThat(currentDefs).extracting(DefinitionJpaEntity::getMeaning)
                .containsExactlyInAnyOrder("수정된뜻1", "새로운뜻1")
                .doesNotContain("기존뜻2");
    }

    @Test
    @DisplayName("기존 뜻 삭제 후 동일한 뜻 재등록 시 성공 (Flush 정합성 검증)")
    void deleteAndReAddSameMeaningSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab).expression("apple").build());
        DefinitionJpaEntity def = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word).meaning("사과").part(PartOfSpeech.NOUN).build());

        em.flush();
        em.clear();

        UpdateWordAndDefinitionsRequestDto request = UpdateWordAndDefinitionsRequestDto.builder()
                .expression("apple")
                .updatingDefinitions(List.of())
                .deletingDefinitions(List.of(def.getId()))
                .addingDefinitions(List.of(new AddDefinitionRequestDto("사과", PartOfSpeech.NOUN)))
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("단어 수정 성공")
    void updateWordSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab)
                .expression("flower")
                .build());

        UpdateWordAndDefinitionsRequestDto request = UpdateWordAndDefinitionsRequestDto.builder()
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

    @Test
    @DisplayName("타인의 정의 ID를 포함하여 수정 요청 시 실패 (보안 검증)")
    void updateWithOtherUserDefinitionIdFail() throws Exception {
        // Given (타인의 단어와 뜻 생성)
        UserJpaEntity otherUser = userJpaRepository.save(UserJpaEntity.builder()
                .email("other@test.com").nickname("other").password("pass").role(UserRole.USER).build());
        VocabularyJpaEntity otherVocab = vocabularyJpaRepository.save(VocabularyJpaEntity.builder()
                .user(otherUser).title("타인단어장").build());
        WordJpaEntity otherWord = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(otherVocab).expression("other").build());
        DefinitionJpaEntity otherDef = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(otherWord).meaning("타인의뜻").part(PartOfSpeech.NOUN).build());

        // 나의 단어 생성
        WordJpaEntity myWord = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab).expression("my").build());

        em.flush();
        em.clear();

        // 나의 단어를 수정하면서 타인의 정의 ID를 슬쩍 끼워넣음
        UpdateWordAndDefinitionsRequestDto request = UpdateWordAndDefinitionsRequestDto.builder()
                .expression("myUpdated")
                .updatingDefinitions(List.of(
                        new UpdateDefinitionRequestDto(otherDef.getId(), "탈취시도", PartOfSpeech.VERB)
                ))
                .addingDefinitions(List.of())
                .deletingDefinitions(List.of())
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + myWord.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then (Reader 레이어의 소유권 검증 로직에 의해 404 에러 발생)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_DEF.getMessage()));
    }

    @Test
    @DisplayName("수정 리스트와 삭제 리스트에 동일 ID 존재 시 실패 (안정성 검증)")
    void duplicateIdInUpdateAndDeleteFail() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab).expression("test").build());
        DefinitionJpaEntity def = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word).meaning("뜻").part(PartOfSpeech.NOUN).build());

        UpdateWordAndDefinitionsRequestDto request = UpdateWordAndDefinitionsRequestDto.builder()
                .expression("test")
                .updatingDefinitions(List.of(new UpdateDefinitionRequestDto(def.getId(), "수정", PartOfSpeech.NOUN)))
                .addingDefinitions(List.of())
                .deletingDefinitions(List.of(def.getId()))
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.INVALID_REQUEST_BODY.getMessage()));
    }

    @Test
    @DisplayName("정합성 테스트 - 뜻 수정 시 자기 자신과의 중복(변경 없음)은 성공해야 함")
    void updateDefinitionWithSameMeaningSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab).expression("apple").build());
        DefinitionJpaEntity def = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word).meaning("사과").part(PartOfSpeech.NOUN).build());

        em.flush();
        em.clear();

        // 내용은 그대로 두고 업데이트 요청
        UpdateWordAndDefinitionsRequestDto request = UpdateWordAndDefinitionsRequestDto.builder()
                .expression("apple")
                .updatingDefinitions(List.of(new UpdateDefinitionRequestDto(def.getId(), "사과", PartOfSpeech.NOUN)))
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("안전성 테스트 - 리스트 필드 누락(Null) 요청 시 DTO Getter가 안전하게 방어하여 성공")
    void updateWordWithNullListsSuccess() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab).expression("apple").build());

        em.flush();
        em.clear();

        // JSON에서 adding/updating/deleting 필드를 아예 생략
        String jsonRequest = "{\"expression\":\"banana\"}";

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                // Then (NPE 없이 200 OK)
                .andExpect(status().isOk());

        WordJpaEntity updatedWord = wordJpaRepository.findById(word.getId()).orElseThrow();
        assertThat(updatedWord.getExpression()).isEqualTo("banana");
    }

    @Test
    @DisplayName("원자성(롤백) 테스트 - 중간 실패 시 예외가 전파되어 트랜잭션 롤백이 예약됨")
    void updateWordRollbackOnPartialFailure() throws Exception {
        // Given
        WordJpaEntity word = wordJpaRepository.save(WordJpaEntity.builder()
                .vocabulary(testVocab).expression("oldWord").build());

        DefinitionJpaEntity defToDelete = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word).meaning("삭제될뜻").part(PartOfSpeech.NOUN).build());
        DefinitionJpaEntity existingDef = definitionJpaRepository.save(DefinitionJpaEntity.builder()
                .word(word).meaning("기존뜻").part(PartOfSpeech.NOUN).build());

        em.flush();
        em.clear();

        // 단어 수정 -> 뜻 삭제 -> 뜻 추가(중복 에러 유발) 시나리오
        UpdateWordAndDefinitionsRequestDto request = UpdateWordAndDefinitionsRequestDto.builder()
                .expression("newWord") // 1. 단어 변경 시도
                .deletingDefinitions(List.of(defToDelete.getId())) // 2. 뜻 삭제 시도
                .addingDefinitions(List.of(new AddDefinitionRequestDto("기존뜻", PartOfSpeech.NOUN))) // 3. 중복 에러 유발
                .build();

        // When
        mockMvc.perform(patch("/api/words/" + word.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then (409 에러가 정상 반환됨을 확인 = 예외가 밖으로 잘 던져짐)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.DUPLICATED_DEFINITION.getMessage()));
    }
}
