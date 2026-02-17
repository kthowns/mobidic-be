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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.UUID;

import static com.kimtaeyang.mobidic.code.AuthResponseCode.UNAUTHORIZED;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.TOO_BIG_FILE_SIZE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class PronunciationIntegrationTest {
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
    @DisplayName("[Pronunciation][Integration] Rate pronunciation test")
    void ratePronunciationTest() throws Exception {
        String token = loginAndGetToken("test@test.com", "test");
        UUID memberId = jwtUtil.getIdFromToken(token);
        UUID vocabId = addVocabAndGetId(memberId, token);
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
        MvcResult result = mockMvc.perform(multipart("/api/pron/rate")
                        .file(file) // 파일 파라미터 추가
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", wordId.toString())) // 문자열 파라미터 추가
                .andExpect(status().isOk()) // 응답 상태 200
                .andReturn();

        String json = result.getResponse().getContentAsString();
        double rate = Double.parseDouble(objectMapper.readTree(json).path("data").asText());

        assertTrue(rate > 0.8);

        //Success low rate
        UUID wordId2 = addWordAndGetId(vocabId, token, "yellow");

        result = mockMvc.perform(multipart("/api/pron/rate")
                        .file(file)
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", wordId2.toString()))
                .andExpect(status().isOk())
                .andReturn();

        json = result.getResponse().getContentAsString();
        rate = Double.parseDouble(objectMapper.readTree(json).path("data").asText());

        assertTrue(rate < 0.8);

        //Fail with too big file
        mockMvc.perform(multipart("/api/pron/rate")
                        .file(largeFile)
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", wordId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(TOO_BIG_FILE_SIZE.getMessage()));

        //Fail without token
        mockMvc.perform(multipart("/api/pron/rate")
                        .file(largeFile)
                        .param("wordId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(multipart("/api/pron/rate")
                        .file(largeFile)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .param("wordId", wordId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(multipart("/api/pron/rate")
                        .file(largeFile)
                        .header("Authorization", "Bearer " + token)
                        .param("wordId", UUID.randomUUID().toString()))
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

    private UUID addWordAndGetId(UUID vocabId, String token, String exp) throws Exception {
        AddWordRequestDto addWordRequest = AddWordRequestDto.builder()
                .expression(exp)
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