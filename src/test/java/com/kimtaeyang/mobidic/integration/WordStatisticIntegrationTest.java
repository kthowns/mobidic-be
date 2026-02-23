package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.AddVocabularyRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.AddWordRequestDto;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
import com.kimtaeyang.mobidic.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static com.kimtaeyang.mobidic.common.code.AuthResponseCode.UNAUTHORIZED;
import static com.kimtaeyang.mobidic.common.code.GeneralResponseCode.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WordStatisticIntegrationTest {
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

    @Test
    @DisplayName("[WordStatistics][Integration] Get rate by word id test")
    void getWordStatisticsByWordIdTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID vocabId = addVocabAndGetId(token);
        UUID wordId = addWordAndGetId(vocabId, token);

        //Success
        mockMvc.perform(get("/api/statistics/word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", wordId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.wordId")
                        .value(wordId.toString()))
                .andExpect(jsonPath("$.data.correctCount")
                        .value(0))
                .andExpect(jsonPath("$.data.incorrectCount")
                        .value(0))
                .andExpect(jsonPath("$.data.learned")
                        .value(false));

        //Fail without token
        mockMvc.perform(get("/api/statistics/word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("wordId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/statistics/word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .param("wordId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/statistics/word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_RATE.getMessage()));
    }

    @Test
    @DisplayName("[WordStatistics][Integration] Get vocab learning rate test")
    void getVocabLearningWordStatisticsTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID vocabularyId = addVocabAndGetId(token);
        UUID wordId = addWordAndGetId(vocabularyId, token);

        //Success
        mockMvc.perform(get("/api/statistics/vocabulary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", vocabularyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data")
                        .value(0.0));

        //Fail without token
        mockMvc.perform(get("/api/statistics/vocabulary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vocabularyId", vocabularyId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/statistics/vocabulary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .param("vocabularyId", vocabularyId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/statistics/vocabulary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("[WordStatistics][Integration] Toggle rate by word id test")
    void toggleWordStatisticsByWordIdTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID vocabId = addVocabAndGetId(token);
        UUID wordId = addWordAndGetId(vocabId, token);

        //Success
        mockMvc.perform(patch("/api/statistics/word/" + wordId + "/learned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/statistics/word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", wordId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.learned")
                        .value(true));

        mockMvc.perform(patch("/api/statistics/word/" + wordId + "/learned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/statistics/word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", wordId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.learned")
                        .value(false));

        //Fail without token
        mockMvc.perform(patch("/api/statistics/word/" + wordId + "/learned")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/statistics/word/" + wordId + "/learned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/statistics/word/" + UUID.randomUUID() + "/learned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_RATE.getMessage()));
    }

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

    private UUID addWordAndGetId(UUID vocabId, String token) throws Exception {
        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("expression")
                .build();

        MvcResult wordResult = mockMvc.perform(post("/api/words/" + vocabId)
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

        mockMvc.perform(post("/api/auth/signup")
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