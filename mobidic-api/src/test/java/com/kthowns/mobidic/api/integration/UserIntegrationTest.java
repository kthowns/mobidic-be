package com.kthowns.mobidic.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kthowns.mobidic.api.security.jwt.JwtProvider;
import com.kthowns.mobidic.api.user.dto.request.UpdateUserRequestDto;
import com.kthowns.mobidic.api.util.DatabaseCleaner;
import com.kthowns.mobidic.common.code.AuthResponseCode;
import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.domain.user.service.UserBlackListService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 사용자 정보 관련 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private jakarta.persistence.EntityManager em;

    @MockitoBean
    private UserBlackListService userBlackListService;

    private UserJpaEntity testUser;
    private String userToken;

    @BeforeEach
    void cleanAndSetup() {
        databaseCleaner.execute();

        testUser = userJpaRepository.save(UserJpaEntity.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("password123!"))
                .role(UserRole.USER)
                .isActive(true)
                .build());

        userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("사용자 상세 정보 조회 성공")
    void getUserDetailsSuccess() throws Exception {
        // When
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("test"));
    }

    @Test
    @DisplayName("사용자 닉네임 수정 성공")
    void updateNicknameSuccess() throws Exception {
        // Given
        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .nickname("newnickname")
                .build();

        // When
        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("newnickname"));

        // Then
        em.flush();
        em.clear();
        UserJpaEntity updatedUser = userJpaRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo("newnickname");
    }

    @Test
    @DisplayName("사용자 닉네임 수정 실패 - 중복된 닉네임")
    void updateNicknameFailDuplicated() throws Exception {
        // Given
        userJpaRepository.saveAndFlush(UserJpaEntity.builder()
                .email("other@test.com")
                .nickname("other")
                .password("pass")
                .role(UserRole.USER)
                .build());

        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .nickname("other")
                .build();

        // When
        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(GeneralResponseCode.DUPLICATED_NICKNAME.getMessage()));
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 성공")
    void updatePasswordSuccess() throws Exception {
        // Given
        UpdateUserRequestDto request = UpdateUserRequestDto.builder()
                .password("newPassword123!")
                .build();

        // When
        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();
        UserJpaEntity updatedUser = userJpaRepository.findById(testUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newPassword123!", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원 탈퇴(비활성화) 성공")
    void withdrawSuccess() throws Exception {
        // When
        mockMvc.perform(delete("/api/users/me")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isOk());

        // Then
        em.flush();
        em.clear();
        UserJpaEntity deactivatedUser = userJpaRepository.findById(testUser.getId()).orElseThrow();
        assertThat(deactivatedUser.isActive()).isFalse();
        assertThat(deactivatedUser.getDeactivatedAt()).isNotNull();
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When
        mockMvc.perform(get("/api/users/me"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                // Then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Autowired
    private com.kthowns.mobidic.api.security.jwt.JwtProperties jwtProperties;

    @Test
    @DisplayName("보안 테스트 - 만료된 토큰으로 요청 시 실패")
    void securityFailExpiredToken() throws Exception {
        // Given
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .subject(testUser.getId().toString())
                .claim("role", testUser.getRole().name())
                .issuedAt(new java.util.Date(System.currentTimeMillis() - 100000))
                .expiration(new java.util.Date(System.currentTimeMillis() - 50000))
                .signWith(jwtProperties.getSecretKey())
                .compact();

        // When
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + expiredToken))
                // Then
                .andExpect(status().isUnauthorized());
        // 프로덕션 코드에서 만료된 토큰에 대한 구체적인 에러 메시지가 다를 수 있으므로 상태 코드만 검증
    }

    @Test
    @DisplayName("보안 테스트 - 탈퇴한(비활성화된) 사용자 토큰으로 요청 시 실패")
    void securityFailDeactivatedUser() throws Exception {
        // Given
        UserJpaEntity user = userJpaRepository.findById(testUser.getId()).orElseThrow();

        // DB 상태 변경 (Soft Delete)
        em.createQuery("update UserJpaEntity u set u.isActive = false where u.id = :id")
                .setParameter("id", user.getId())
                .executeUpdate();

        em.clear();

        // Mock 설정: 해당 유저 ID 조회 시 블랙리스트에 있다고 가정
        given(userBlackListService.isDeactivatedUser(testUser.getId())).willReturn(true);

        // When
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + userToken))
                // Then
                .andExpect(status().isUnauthorized());
    }
}
