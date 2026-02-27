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
public class WordIntegrationTest {
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
    @DisplayName("[Word][Integration] Add word test")
    void addWordTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID vocabularyId = addVocabAndGetVocabId(token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();

        //Success
        mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.expression")
                        .value(addWordRequest.getExpression()));

        //Fail with duplicated word
        mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_WORD.getMessage()));

        //Fail without token
        mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(post("/api/words/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_VOCAB.getMessage()));

        //Fail with invalid pattern
        addWordRequest.setExpression(UUID.randomUUID().toString() + UUID.randomUUID());
        mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(
                        jsonPath("$.errors.expression")
                                .value("Invalid expression pattern"));
    }

    @Test
    @DisplayName("[Word][Integration] Get word by vocab id test")
    void getWordByVocabTestId() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID vocabularyId = addVocabAndGetVocabId(token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();

        mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk());

        //Success
        mockMvc.perform(get("/api/words/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", vocabularyId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].expression")
                        .value(addWordRequest.getExpression()))
                .andExpect(jsonPath("$.data[0].vocabularyId")
                        .value(vocabularyId.toString()))
                .andExpect(jsonPath("$.data[0].createdAt")
                        .isNotEmpty());

        //Fail without token
        mockMvc.perform(get("/api/words/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vocabularyId", vocabularyId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/words/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vocabularyId", vocabularyId.toString())
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/words/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vocabularyId", UUID.randomUUID().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("[Word][Integration] Update word test")
    void updateWordTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID vocabularyId = addVocabAndGetVocabId(token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();
        AddWordRequestDto addWordRequest2 = AddWordRequestDto.builder()
                .expression("testtest")
                .build();

        MvcResult addWordResult = mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult addWordResult2 = mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest2)))
                .andExpect(status().isOk())
                .andReturn();

        String json = addWordResult.getResponse().getContentAsString();
        String wordId = objectMapper.readTree(json).path("data").path("id").asText();
        json = addWordResult2.getResponse().getContentAsString();
        String wordId2 = objectMapper.readTree(json).path("data").path("id").asText();

        //Success
        mockMvc.perform(patch("/api/words/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.expression")
                        .value(addWordRequest.getExpression()));

        //Fail with duplicated word
        mockMvc.perform(patch("/api/words/" + wordId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_WORD.getMessage()));

        //Fail without token
        mockMvc.perform(patch("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/words/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_WORD.getMessage()));

        //Fail with invalid pattern
        addWordRequest.setExpression(UUID.randomUUID().toString() + UUID.randomUUID());
        mockMvc.perform(patch("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(
                        jsonPath("$.errors.expression")
                                .value("Invalid expression pattern"));
    }

    @Test
    @DisplayName("[Word][Integration] Delete word test")
    void deleteWordTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID vocabularyId = addVocabAndGetVocabId(token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();

        MvcResult addWordResult = mockMvc.perform(post("/api/words/" + vocabularyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = addWordResult.getResponse().getContentAsString();
        String wordId = objectMapper.readTree(json).path("data").path("id").asText();

        //Fail without token
        mockMvc.perform(delete("/api/words/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(delete("/api/words/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
        //Success
        mockMvc.perform(delete("/api/words/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(wordId))
                .andExpect(jsonPath("$.data.expression")
                        .value(addWordRequest.getExpression()))
                .andExpect(jsonPath("$.data.vocabularyId")
                        .value(vocabularyId.toString()));

        //Fail with no resource
        mockMvc.perform(delete("/api/words/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_WORD.getMessage()));
    }

    private UUID addVocabAndGetVocabId(String token) throws Exception {
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

        String vocabularyId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asText();

        return UUID.fromString(vocabularyId);
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