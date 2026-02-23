package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.AddVocabularyRequestDto;
import com.kimtaeyang.mobidic.dictionary.dto.VocabularyDto;
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
public class VocabularyIntegrationTest {
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
    @DisplayName("[Vocab][Integration] Add vocab test")
    void addVocabTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);

        AddVocabularyRequestDto addVocabRequest = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        //Success
        mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title")
                        .value(addVocabRequest.getTitle()))
                .andExpect(jsonPath("$.data.description")
                        .value(addVocabRequest.getDescription()))
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty());

        //Fail with duplicated title
        mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_TITLE.getMessage()));

        //Fail without token
        mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.message")
                                .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(addVocabRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.message")
                                .value(UNAUTHORIZED.getMessage()));

        //Fail with invalid pattern
        addVocabRequest.setTitle(UUID.randomUUID().toString());
        addVocabRequest.setDescription(UUID.randomUUID().toString() + UUID.randomUUID());
        mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addVocabRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(
                        jsonPath("$.errors.title")
                                .value("Invalid title pattern"))
                .andExpect(
                        jsonPath("$.errors.description")
                                .value("Invalid description pattern"));
    }

    @Test
    @DisplayName("[Vocab][Integration] Get vocabs test")
    void getVocabsByUserIdTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);

        AddVocabularyRequestDto addVocabRequest = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        //Success
        mockMvc.perform(get("/api/vocabularies/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title")
                        .value(addVocabRequest.getTitle()))
                .andExpect(jsonPath("$.data[0].description")
                        .value(addVocabRequest.getDescription()));

        //Fail without token
        mockMvc.perform(get("/api/vocabularies/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/vocabularies/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("[Vocab][Integration] Get vocabs test")
    void getVocabsByIdTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID userId = jwtProvider.getIdFromToken(token);

        AddVocabularyRequestDto addVocabRequest = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        MvcResult addResult = mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String json = addResult.getResponse().getContentAsString();
        VocabularyDto addVocabResponse = objectMapper.convertValue(
                objectMapper.readTree(json).path("data"), VocabularyDto.class
        );

        //Success
        mockMvc.perform(get("/api/vocabularies/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", addVocabResponse.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(addVocabResponse.getId().toString()))
                .andExpect(jsonPath("$.data.userId")
                        .value(userId.toString()))
                .andExpect(jsonPath("$.data.title")
                        .value(addVocabRequest.getTitle()))
                .andExpect(jsonPath("$.data.description")
                        .value(addVocabRequest.getDescription()))
                .andExpect(jsonPath("$.data.createdAt")
                        .isNotEmpty());

        //Fail without token
        mockMvc.perform(get("/api/vocabularies/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vocabularyId", addVocabResponse.getId().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/vocabularies/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .param("vocabularyId", addVocabResponse.getId().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/vocabularies/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_VOCAB.getMessage()));
    }

    @Test
    @DisplayName("[Vocab][Integration] Update vocab test")
    void updateVocabTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);

        AddVocabularyRequestDto addVocabRequest = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();
        AddVocabularyRequestDto addVocabRequest2 = AddVocabularyRequestDto.builder()
                .title("title2")
                .description("description2")
                .build();

        MvcResult addResult = mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult addResult2 = mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest2))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String json = addResult.getResponse().getContentAsString();
        VocabularyDto addVocabResponse = objectMapper.convertValue(
                objectMapper.readTree(json).path("data"), VocabularyDto.class
        );
        json = addResult2.getResponse().getContentAsString();
        VocabularyDto addVocabResponse2 = objectMapper.convertValue(
                objectMapper.readTree(json).path("data"), VocabularyDto.class
        );

        AddVocabularyRequestDto updateVocabRequest = AddVocabularyRequestDto.builder()
                .title(addVocabRequest.getTitle())
                .description("description3")
                .build();

        //Success
        mockMvc.perform(patch("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateVocabRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title")
                        .value(updateVocabRequest.getTitle()))
                .andExpect(jsonPath("$.data.description")
                        .value(updateVocabRequest.getDescription()));

        mockMvc.perform(patch("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateVocabRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title")
                        .value(updateVocabRequest.getTitle()))
                .andExpect(jsonPath("$.data.description")
                        .value(updateVocabRequest.getDescription()));

        //Fail with duplicated title
        mockMvc.perform(patch("/api/vocabularies/" + addVocabResponse2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateVocabRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_TITLE.getMessage()));

        //Fail without token
        mockMvc.perform(patch("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateVocabRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(updateVocabRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/vocabularies/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateVocabRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_VOCAB.getMessage()));

        //Fail with invalid pattern
        updateVocabRequest.setTitle(UUID.randomUUID().toString());
        updateVocabRequest.setDescription(UUID.randomUUID().toString() + UUID.randomUUID());
        mockMvc.perform(patch("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateVocabRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors.title")
                        .value("Invalid title pattern"))
                .andExpect(jsonPath("$.errors.description")
                        .value("Invalid description pattern"));
    }

    @Test
    @DisplayName("[Vocab][Integration] Delete vocab test")
    void deleteVocabTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);

        AddVocabularyRequestDto addVocabRequest = AddVocabularyRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        MvcResult addResult = mockMvc.perform(post("/api/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String json = addResult.getResponse().getContentAsString();
        VocabularyDto addVocabResponse = objectMapper.convertValue(
                objectMapper.readTree(json).path("data"), VocabularyDto.class
        );

        //Fail without token
        mockMvc.perform(delete("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vocabId", addVocabResponse.getId().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(delete("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtProvider.generateToken(UUID.randomUUID()))
                        .param("vocabId", addVocabResponse.getId().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(delete("/api/vocabularies/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabId", addVocabResponse.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_VOCAB.getMessage()));

        //Success
        mockMvc.perform(delete("/api/vocabularies/" + addVocabResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(addVocabResponse.getId().toString()))
                .andExpect(jsonPath("$.data.title")
                        .value(addVocabRequest.getTitle()))
                .andExpect(jsonPath("$.data.description")
                        .value(addVocabRequest.getDescription()))
                .andExpect(jsonPath("$.data.createdAt")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.userId")
                        .isNotEmpty());

        mockMvc.perform(get("/api/vocabularies/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vocabularyId", addVocabResponse.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(NO_VOCAB.getMessage()));
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