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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class RateIntegrationTest {
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
    @DisplayName("[Rate][Integration] Get rate by word id test")
    void getRateByWordIdTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);
        UUID wordId = addWordAndGetId(vocabId, token);

        //Success
        mockMvc.perform(get("/api/rate/w")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wId", wordId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.wordId")
                        .value(wordId.toString()))
                .andExpect(jsonPath("$.data.correctCount")
                        .value(0))
                .andExpect(jsonPath("$.data.incorrectCount")
                        .value(0))
                .andExpect(jsonPath("$.data.isLearned")
                        .value(0));

        //Fail without token
        mockMvc.perform(get("/api/rate/w")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("wId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/rate/w")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .param("wId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/rate/w")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wId", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("[Rate][Integration] Get vocab learning rate test")
    void getVocabLearningRateTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);
        UUID wordId = addWordAndGetId(vocabId, token);

        //Success
        mockMvc.perform(get("/api/rate/v")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data")
                        .value(0.0));

        //Fail without token
        mockMvc.perform(get("/api/rate/v")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("vId", vocabId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/rate/v")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .param("vId", vocabId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/rate/v")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("vId", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("[Vocab][Integration] Toggle rate by word id test")
    void toggleRateByWordIdTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);
        UUID wordId = addWordAndGetId(vocabId, token);

        //Success
        mockMvc.perform(patch("/api/rate/tog/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/rate/w")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wId", wordId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLearned")
                        .value(1));

        mockMvc.perform(patch("/api/rate/tog/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/rate/w")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("wId", wordId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isLearned")
                        .value(0));

        //Fail without token
        mockMvc.perform(patch("/api/rate/tog/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/rate/tog/" + wordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/rate/tog/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    private UUID addVocabAndGetId(UUID memberId, String token) throws Exception {
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

    private UUID addWordAndGetId(UUID vocabId, String token) throws Exception {
        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression("expression")
                .build();

        MvcResult wordResult = mockMvc.perform(post("/api/word/" + vocabId)
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