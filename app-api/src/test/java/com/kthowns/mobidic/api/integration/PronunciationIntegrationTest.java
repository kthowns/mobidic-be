package com.kthowns.mobidic.api.integration;

import com.kthowns.mobidic.security.util.JwtProvider;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.pronunciation.client.SpeechToTextClient;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.word.model.Word;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 발음 평가 관련 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PronunciationIntegrationTest {

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoBean
    private SpeechToTextClient speechToTextClient;

    private UserJpaEntity testUser;
    private String userToken;
    private WordJpaEntity testWord;

    @BeforeEach
    void setup() {
        transactionTemplate.execute(status -> {
            testUser = userJpaRepository.save(UserJpaEntity.createFromModel(
                    User.create("test@test.com", "test", passwordEncoder.encode("password123!"), UserRole.USER)));

            VocabularyJpaEntity vocabulary = vocabularyJpaRepository.save(VocabularyJpaEntity.createFromModel(
                    Vocabulary.create(testUser.getId(), "테스트 단어장", null, 0L)));

            testWord = wordJpaRepository.save(WordJpaEntity.createFromModel(
                    Word.create(vocabulary.getId(), "apple"), vocabulary));
            return null;
        });

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.execute(status -> {
            wordJpaRepository.deleteAllInBatch();
            vocabularyJpaRepository.deleteAllInBatch();
            userJpaRepository.deleteAllInBatch();
            return null;
        });
    }

    @Test
    @DisplayName("발음 평가 성공")
    void evaluatePronunciationSuccess() throws Exception {
        // Given
        given(speechToTextClient.transcribe(any())).willReturn("apple");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.m4a", "audio/m4a", "dummy audio content".getBytes());

        // When
        mockMvc.perform(multipart("/api/words/" + testWord.getId() + "/pronunciation")
                        .file(file)
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1.0)); // 정확히 일치하므로 점수 1.0
    }

    @Test
    @DisplayName("발음 평가 실패 - 너무 큰 파일")
    void evaluatePronunciationFailFileSizeExceeded() throws Exception {
        // Given
        byte[] largeContent = new byte[501 * 1024];
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "large.m4a", "audio/m4a", largeContent);

        // When
        mockMvc.perform(multipart("/api/words/" + testWord.getId() + "/pronunciation")
                        .file(largeFile)
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.TOO_BIG_FILE_SIZE.getMessage()));
    }

    @Test
    @DisplayName("발음 평가 실패 - 존재하지 않는 단어")
    void evaluatePronunciationFailNoWord() throws Exception {
        // Given
        UUID randomId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.m4a", "audio/m4a", "dummy content".getBytes());

        // When
        mockMvc.perform(multipart("/api/words/" + randomId + "/pronunciation")
                        .file(file)
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.NO_WORD.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.m4a", "audio/m4a", "dummy content".getBytes());

        // When
        mockMvc.perform(multipart("/api/words/" + testWord.getId() + "/pronunciation")
                        .file(file))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.m4a", "audio/m4a", "dummy content".getBytes());

        // When
        mockMvc.perform(multipart("/api/words/" + testWord.getId() + "/pronunciation")
                        .file(file)
                        .header("Authorization", "Bearer invalid-token"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
