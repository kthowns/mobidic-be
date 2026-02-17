package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.dto.member.JoinRequestDto;
import com.kimtaeyang.mobidic.dto.member.LoginDto;
import com.kimtaeyang.mobidic.dto.member.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.dto.member.UpdatePasswordRequestDto;
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
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.DUPLICATED_NICKNAME;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.INVALID_REQUEST_BODY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class MemberIntegrationTest {
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
    @DisplayName("[Member][Integration] Get member detail")
    void getMemberDetailTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);

        //Success
        mockMvc.perform(get("/api/user/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("uId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(memberId.toString()))
                .andExpect(jsonPath("$.data.email")
                        .value(email))
                .andExpect(jsonPath("$.data.nickname")
                        .value(nickname))
                .andExpect(jsonPath("$.data.createdAt")
                        .isNotEmpty());

        //Fail without token
        mockMvc.perform(get("/api/user/detail/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(get("/api/user/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .param("uId", memberId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(get("/api/user/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .param("uId", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("[Member][Integration] Update member nickname")
    void updateMemberNicknameTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);

        String email2 = "test2@test.com";
        String nickname2 = "test2";
        String token2 = loginAndGetToken(email2, nickname2);
        UUID memberId2 = jwtUtil.getIdFromToken(token2);

        //Success
        UpdateNicknameRequestDto updateNicknameRequest = UpdateNicknameRequestDto.builder()
                .nickname(nickname + "test")
                .build();

        mockMvc.perform(patch("/api/user/nckchn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateNicknameRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(memberId.toString()))
                .andExpect(jsonPath("$.data.nickname")
                        .value(updateNicknameRequest.getNickname()));

        mockMvc.perform(patch("/api/user/nckchn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateNicknameRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(memberId.toString()))
                .andExpect(jsonPath("$.data.nickname")
                        .value(updateNicknameRequest.getNickname()));

        mockMvc.perform(get("/api/user/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("uId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname")
                        .value(updateNicknameRequest.getNickname()));

        //Fail with duplicated nickname
        mockMvc.perform(patch("/api/user/nckchn/" + memberId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2)
                        .content(objectMapper.writeValueAsString(updateNicknameRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(DUPLICATED_NICKNAME.getMessage()));

        //Fail without token
        mockMvc.perform(patch("/api/user/nckchn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNicknameRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/user/nckchn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(updateNicknameRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/user/nckchn/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateNicknameRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with invalid nickname pattern
        updateNicknameRequest.setNickname(nickname + "testqweqweqqq");
        mockMvc.perform(patch("/api/user/nckchn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateNicknameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors.nickname")
                        .value("Invalid nickname pattern"));

    }

    @Test
    @DisplayName("[Member][Integration] Update member password")
    void updateMemberPasswordTest() throws Exception {
        String email = "test@test.com";
        String nickname = "test";
        String token = loginAndGetToken(email, nickname);
        UUID memberId = jwtUtil.getIdFromToken(token);

        UpdatePasswordRequestDto updatePasswordRequest = UpdatePasswordRequestDto.builder()
                .password("testTest2")
                .build();

        //Fail without token
        mockMvc.perform(patch("/api/user/pschn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with unauthorized token
        mockMvc.perform(patch("/api/user/pschn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(UUID.randomUUID()))
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with no resource
        mockMvc.perform(patch("/api/user/pschn/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));

        //Fail with invalid nickname pattern
        updatePasswordRequest.setPassword("test");
        mockMvc.perform(patch("/api/user/pschn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors.password")
                        .value("Invalid password pattern"));

        updatePasswordRequest.setPassword("testTest2");

        //Success
        mockMvc.perform(patch("/api/user/pschn/" + memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("uId", memberId.toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(UNAUTHORIZED.getMessage()));
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