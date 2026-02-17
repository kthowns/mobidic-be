package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.dto.AddVocabRequestDto;
import com.kimtaeyang.mobidic.dto.AddWordRequestDto;
import com.kimtaeyang.mobidic.dto.member.JoinRequestDto;
import com.kimtaeyang.mobidic.dto.member.LoginDto;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import com.kimtaeyang.mobidic.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
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

import static com.kimtaeyang.mobidic.code.AuthResponseCode.UNAUTHORIZED;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.DUPLICATED_WORD;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.INVALID_REQUEST_BODY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class WordIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("[Word][Integration] Add word test")
    void addWordTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetVocabId(memberId, token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();

        //Success
        mockMvc.perform(post("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.expression")
                        .value(addWordRequest.getExpression()));

        //Fail with duplicated word
        mockMvc.perform(post("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_WORD.getMessage()));

        //Fail without token
        mockMvc.perform(post("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(post("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(post("/api/word/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with invalid pattern
        addWordRequest.setExpression(UUID.randomUUID().toString() + UUID.randomUUID());
        mockMvc.perform(post("/api/word/" + vocabId)
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
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetVocabId(memberId, token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();

        mockMvc.perform(post("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk());

        //Success
        mockMvc.perform(get("/api/word/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].expression")
                        .value(addWordRequest.getExpression()))
                .andExpect(jsonPath("$.data[0].vocabId")
                        .value(vocabId.toString()))
                .andExpect(jsonPath("$.data[0].createdAt")
                        .isNotEmpty());

        //Fail without token
        mockMvc.perform(get("/api/word/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/word/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vId", vocabId.toString())
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/word/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vId", UUID.randomUUID().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("[Word][Integration] Update word test")
    void updateWordTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetVocabId(memberId, token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();
        AddWordRequestDto addWordRequest2 = AddWordRequestDto.builder()
                .expression("testtest")
                .build();

        MvcResult addWordResult = mockMvc.perform(post("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult addWordResult2 = mockMvc.perform(post("/api/word/" + vocabId)
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
        mockMvc.perform(patch("/api/word/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.expression")
                        .value(addWordRequest.getExpression()));

        //Fail with duplicated word
        mockMvc.perform(patch("/api/word/" + wordId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_WORD.getMessage()));

        //Fail without token
        mockMvc.perform(patch("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/word/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with invalid pattern
        addWordRequest.setExpression(UUID.randomUUID().toString() + UUID.randomUUID());
        mockMvc.perform(patch("/api/word/" + vocabId)
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
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetVocabId(memberId, token);

        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("test")
                .build();

        MvcResult addWordResult = mockMvc.perform(post("/api/word/" + vocabId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(addWordRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = addWordResult.getResponse().getContentAsString();
        String wordId = objectMapper.readTree(json).path("data").path("id").asText();

        //Fail without token
        mockMvc.perform(delete("/api/word/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(delete("/api/word/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
        //Success
        mockMvc.perform(delete("/api/word/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(wordId))
                .andExpect(jsonPath("$.data.expression")
                        .value(addWordRequest.getExpression()))
                .andExpect(jsonPath("$.data.vocabId")
                        .value(vocabId.toString()));

        //Fail with no resource
        mockMvc.perform(delete("/api/word/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    private UUID addVocabAndGetVocabId(UUID memberId, String token) throws Exception {
        AddVocabRequestDto addVocabRequest = AddVocabRequestDto.builder()
                .title("title")
                .description("description")
                .build();

        MvcResult result = mockMvc.perform(post("/api/vocab/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addVocabRequest))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String vocabId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asText();

        return UUID.fromString(vocabId);
    }

    private String loginAndGetToken(String email, String nickname) throws Exception {
        JoinRequestDto joinRequest = JoinRequestDto.builder()
                .email(email)
                .nickname(nickname)
                .password("testTest1")
                .build();

        LoginDto.Request loginRequest = LoginDto.Request.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();

        mockMvc.perform(post("/api/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = loginResult.getResponse().getContentAsString();
        return objectMapper.readTree(json).path("data").path("token").asText();
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