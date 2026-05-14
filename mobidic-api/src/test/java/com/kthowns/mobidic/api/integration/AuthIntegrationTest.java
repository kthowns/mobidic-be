package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.dto.request.auth.LoginRequest;
import com.kthowns.mobidic.api.dto.request.user.SignUpRequestDto;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
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

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
    }

    @DisplayName("[Auth][Integration] Join test")
    @Test
    void joinTest() throws Exception {
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("test@test.com")
                .nickname("test")
                .password("testTest1!")
                .agreeTermIds(List.of())
                .build();

        //Success
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        //Fail with duplicated Email
        request.setNickname("test2");
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(GeneralResponseCode.DUPLICATED_EMAIL.getMessage()));

        /*
        //Fail with duplicated Nickname
        request.setEmail("test2@test.com");
        request.setNickname("test");
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(GeneralResponseCode.DUPLICATED_NICKNAME.getMessage()));
         */

        //Email, nickname, password format fail
        request.setEmail("test@test");
        request.setNickname("1");
        request.setPassword("test");

        HashMap<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("email", "유효하지 않은 이메일 형식입니다.");
        expectedErrors.put("nickname", "닉네임은 2~16자의 한글, 영문 소문자, 숫자, -, _ 만 사용할 수 있습니다.");
        expectedErrors.put("password", "비밀번호는 8~128자이며 영문자, 숫자, 특수문자(@$!%*?&)를 각각 1개 이상 포함해야 합니다.");

        mockMvc.perform(post("/api/users/signup")
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
                .password("qwerqwe1!")
                .agreeTermIds(List.of())
                .build();

        //SignUp
        mockMvc.perform(post("/api/users/signup")
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
                        .value("유효하지 않은 이메일 형식입니다."));
    }

    @DisplayName("[Auth][Integration] Logout test")
    @Test
    void logoutTest() throws Exception {
        SignUpRequestDto joinRequest = SignUpRequestDto.builder()
                .email("test@test.com")
                .nickname("test")
                .password("testTest1!")
                .agreeTermIds(List.of())
                .build();

        mockMvc.perform(post("/api/users/signup")
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

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        //Testing post method through invalid token
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value(AuthResponseCode.INVALID_TOKEN.getMessage()));
    }
}
