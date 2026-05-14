package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.dto.request.auth.LoginRequest;
import com.kthowns.mobidic.api.dto.request.dictionary.AddVocabularyRequestDto;
import com.kthowns.mobidic.api.dto.request.dictionary.AddWordRequestDto;
import com.kthowns.mobidic.api.dto.request.user.SignUpRequestDto;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PronunciationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void tearDown() {
        databaseCleaner.execute();
    }

    /*
        @Test
        @DisplayName("[Pronunciation][Integration] Rate pronunciation test")
        void ratePronunciationTest() throws Exception {
            String token = loginAndGetToken("test@test.com", "test");
            UUID vocabId = addVocabAndGetId(token);
            UUID wordId = addWordAndGetId(vocabId, token, "hello");

            MockMultipartFile file = new MockMultipartFile(
                    "file", // 파일 파라미터 이름
                    "hello.m4a", // 파일 이름
                    "audio/m4a", // 파일 MIME 타입
                    new FileInputStream(Paths.get("src/test/resources/hello.m4a").toFile()) // 상대 경로
            );

            MockMultipartFile largeFile = new MockMultipartFile(
                    "file", // 파일 파라미터 이름
                    "napal.mp3", // 파일 이름
                    "audio/mp3", // 파일 MIME 타입
                    new FileInputStream(Paths.get("src/test/resources/napal.mp3").toFile()) // 상대 경로
            );

            //Success high rate
            MvcResult result = mockMvc.perform(multipart("/api/words/" + wordId + "/pronunciation")
                            .file(file) // 파일 파라미터 추가
                            .header("Authorization", "Bearer " + token)) // 문자열 파라미터 추가
                    .andExpect(status().isOk()) // 응답 상태 200
                    .andExpect(jsonPath("$.data")
                            .isNotEmpty())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            double rate = Double.parseDouble(objectMapper.readTree(json).path("data").asText());

            assertTrue(rate > 0.8);

            //Success low rate
            UUID wordId2 = addWordAndGetId(vocabId, token, "yellow");

            result = mockMvc.perform(multipart("/api/words/" + wordId2 + "/pronunciation")
                            .file(file)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

            json = result.getResponse().getContentAsString();
            rate = Double.parseDouble(objectMapper.readTree(json).path("data").asText());

            assertTrue(rate < 0.8);

            //Fail with too big file
            mockMvc.perform(multipart("/api/words/" + wordId + "/pronunciation")
                            .file(largeFile)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(TOO_BIG_FILE_SIZE.getMessage()));

            //Fail without token
            mockMvc.perform(multipart("/api/words/" + wordId + "/pronunciation")
                            .file(largeFile))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message")
                            .value(AuthResponseCode.UNAUTHORIZED.getMessage()));

            //Fail with unauthorized token
            mockMvc.perform(multipart("/api/words/" + wordId + "/pronunciation")
                            .file(largeFile)
                            .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID())))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message")
                            .value(AuthResponseCode.UNAUTHORIZED.getMessage()));

            //Fail with no resource
            mockMvc.perform(multipart("/api/words/" + UUID.randomUUID() + "/pronunciation")
                            .file(file)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value(GeneralResponseCode.NO_WORD.getMessage()));
        }
    */
    private UUID addVocabAndGetId(String token) throws Exception {
        AddVocabularyRequestDto addVocabRequest = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        MvcResult result = mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String vocabId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asText();

        return UUID.fromString(vocabId);
    }

    private UUID addWordAndGetId(UUID vocabId, String token, String exp) throws Exception {
        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression(exp)
                .build();

        MvcResult wordResult = mockMvc.perform(post("/api/vocabularies/" + vocabId + "/word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String wordId = objectMapper.readTree(wordResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        return UUID.fromString(wordId);
    }

    private String loginAndGetToken(String email, String nickname) throws Exception {
        SignUpRequestDto joinRequest = SignUpRequestDto.builder()
                .email(email)
                .nickname(nickname)
                .password("testTest1")
                .build();

        LoginRequest loginRequest = LoginRequest.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = loginResult.getResponse().getContentAsString();
        return objectMapper.readTree(json).path("data").path("accessToken").asText();
    }
}

// Resource api integration test convention
//Success
// -> OK
//Fail without token
// -> UNAUTHORIZED
//Fail with unauthorized token
// -> UNAUTHORIZED
//Fail with no resource
// -> UNAUTHORIZED
//Fail with invalid pattern
// -> INVALID_REQUEST_BODY