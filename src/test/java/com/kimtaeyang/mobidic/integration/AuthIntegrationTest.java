package com.kimtaeyang.mobidic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtaeyang.mobidic.auth.dto.LoginRequest;
import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.common.code.AuthResponseCode;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.security.jwt.JwtProvider;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @DisplayName("[Auth][Integration] Join test")
    @Test
    @Transactional
    void joinTest() throws Exception {
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("test@test.com")
                .nickname("test")
                .password("testTest1")
                .build();

        //Success
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email")
                        .value("test@test.com"))
                .andExpect(jsonPath("$.data.nickname")
                        .value("test"));

        //Fail with duplicated Email
        request.setNickname("test2");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(GeneralResponseCode.DUPLICATED_EMAIL.getMessage()));

        //Fail with duplicated Nickname
        request.setEmail("test2@test.com");
        request.setNickname("test");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(GeneralResponseCode.DUPLICATED_NICKNAME.getMessage()));

        //Email, nickname, password format fail
        request.setEmail("test@test");
        request.setNickname("1");
        request.setPassword("test");

        HashMap<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("email", "Invalid email pattern");
        expectedErrors.put("nickname", "Invalid nickname pattern");
        expectedErrors.put("password", "Invalid password pattern");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(GeneralResponseCode.INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors")
                        .value(expectedErrors));
    }

    @DisplayName("[Auth][Integration] Login test")
    @Test
    void loginTest() throws Exception {
        SignUpRequestDto joinRequest = SignUpRequestDto.builder()
                .email("qwerq@test.com")
                .nickname("qwerq")
                .password("qwerqwe1")
                .build();

        //SignUp
        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = LoginRequest.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();

        //Login success
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(json).path("data").path("accessToken").asText();

        assertThat(jwtProvider.validateToken(token));

        loginRequest.setPassword("wrongPassword");

        //Login fail invalid password
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(AuthResponseCode.LOGIN_FAILED.getMessage()));

        //Login fail invalid email
        loginRequest.setEmail("wrong@email.com");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(AuthResponseCode.LOGIN_FAILED.getMessage()));

        //Login fail invalid email pattern
        loginRequest.setEmail("wrong");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(GeneralResponseCode.INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors.email")
                        .value("Invalid email pattern"));
    }

    @DisplayName("[Auth][Integration] Logout test")
    @Test
    void logoutTest() throws Exception {
        SignUpRequestDto joinRequest = SignUpRequestDto.builder()
                .email("test@test.com")
                .nickname("test")
                .password("testTest1")
                .build();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = LoginRequest.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        //Logout success
        String loginJson = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(loginJson).path("data").path("accessToken").asText();

        UUID memberId = jwtProvider.getIdFromToken(token);

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(memberId.toString()));

        //Testing post method through invalid token
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }
}
