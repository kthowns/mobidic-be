package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.auth.dto.request.LoginRequest;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.user.dto.request.SignUpRequestDto;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 인증 관련 통합 테스트
 * 회원가입 및 로그인 기능을 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("회원가입 성공 - 유효한 정보를 입력하면 새로운 사용자가 생성된다.")
    void joinSuccess() throws Exception {
        // Given: 유효한 회원가입 요청 데이터 준비
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("test@test.com")
                .nickname("test")
                .password("testTest1!")
                .agreeTermIds(List.of())
                .build();

        // When: 회원가입 API 호출
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then (1): HTTP 응답 상태 코드 검증
                .andExpect(status().isOk());

        // Then (2): 실제 DB에 데이터가 정상적으로 저장되었는지 확인
        UserJpaEntity savedUser = userJpaRepository.findByEmail("test@test.com").orElseThrow();
        assertThat(savedUser.getNickname()).isEqualTo("test");
        assertThat(passwordEncoder.matches("testTest1!", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일로 가입할 수 없다.")
    void joinFailDuplicatedEmail() throws Exception {
        // Given: 이미 가입된 사용자가 존재하는 상황
        userJpaRepository.save(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("other")
                .password(passwordEncoder.encode("password123!"))
                .build());

        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("test@test.com")
                .nickname("test")
                .password("testTest1!")
                .agreeTermIds(List.of())
                .build();

        // When: 중복된 이메일로 회원가입 API 호출
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then: 409 Conflict 응답 및 에러 메시지 확인
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.DUPLICATED_EMAIL.getMessage()));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임으로 가입할 수 없다.")
    void joinFailDuplicatedNickname() throws Exception {
        // Given: 이미 사용 중인 닉네임이 존재하는 상황
        userJpaRepository.save(UserJpaEntity.builder()
                .email("other@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .build());

        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("test@test.com")
                .nickname("test")
                .password("testTest1!")
                .agreeTermIds(List.of())
                .build();

        // When: 중복된 닉네임으로 회원가입 API 호출
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then: 409 Conflict 응답 및 에러 메시지 확인
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.DUPLICATED_NICKNAME.getMessage()));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효하지 않은 입력값(이메일, 닉네임, 비밀번호 형식)")
    void joinFailInvalidInput() throws Exception {
        // Given: 유효하지 않은 형식의 데이터 준비
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("test@test")
                .nickname("1")
                .password("test")
                .agreeTermIds(List.of())
                .build();

        HashMap<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("email", "유효하지 않은 이메일 형식입니다.");
        expectedErrors.put("nickname", "닉네임은 2~16자의 한글, 영문 소문자, 숫자, -, _ 만 사용할 수 있습니다.");
        expectedErrors.put("password", "비밀번호는 8~128자이며 영문자, 숫자, 특수문자(@$!%*?&)를 각각 1개 이상 포함해야 합니다.");

        // When: 유효하지 않은 데이터로 회원가입 API 호출
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then: 400 Bad Request 응답 및 상세 필드 에러 확인
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors").value(expectedErrors));
    }

    @Test
    @DisplayName("로그인 성공 - 올바른 자격 증명을 입력하면 액세스 토큰을 반환한다.")
    void loginSuccess() throws Exception {
        // Given: 가입된 사용자가 존재하는 상황
        userJpaRepository.save(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .build());

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("password123!")
                .build();

        // When: 로그인 API 호출
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Then: 200 Ok 응답 및 반환된 토큰 유효성 검증
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(json).path("data").path("accessToken").asText();
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("로그인 실패 - 틀린 비밀번호로는 로그인할 수 없다.")
    void loginFailWrongPassword() throws Exception {
        // Given: 가입된 사용자가 존재하는 상황
        userJpaRepository.save(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .build());

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("wrongPassword")
                .build();

        // When: 틀린 비밀번호로 로그인 API 호출
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Then: 401 Unauthorized 응답 및 에러 메시지 확인
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.LOGIN_FAILED.getMessage()));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일로는 로그인할 수 없다.")
    void loginFailNonExistentEmail() throws Exception {
        // Given: 존재하지 않는 이메일 정보
        LoginRequest loginRequest = LoginRequest.builder()
                .email("nonexistent@test.com")
                .password("password123!")
                .build();

        // When: 가입되지 않은 이메일로 로그인 API 호출
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Then: 401 Unauthorized 응답 확인
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.LOGIN_FAILED.getMessage()));
    }

    @Test
    @DisplayName("로그인 실패 - 유효하지 않은 이메일 형식으로 로그인 시도 시 에러가 발생한다.")
    void loginFailInvalidEmailFormat() throws Exception {
        // Given: 유효하지 않은 이메일 패턴
        LoginRequest loginRequest = LoginRequest.builder()
                .email("wrong")
                .password("password123!")
                .build();

        // When: 유효하지 않은 이메일 형식으로 로그인 API 호출
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Then: 400 Bad Request 응답 및 상세 필드 에러 확인
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.INVALID_REQUEST_BODY.getMessage()))
                .andExpect(jsonPath("$.errors.email").value("유효하지 않은 이메일 형식입니다."));
    }
}
