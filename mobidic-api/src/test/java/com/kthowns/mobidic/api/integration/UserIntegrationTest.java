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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoBean
    private UserBlackListService userBlackListService;

    private UserJpaEntity testUser;
    private String userToken;

    @BeforeAll
    void cleanAndSetup() {
        transactionTemplate.execute(status -> {
            databaseCleaner.execute();

            testUser = userJpaRepository.save(UserJpaEntity.builder()
                    .email("test@test.com")
                    .nickname("test")
                    .password(passwordEncoder.encode("password123!"))
                    .role(UserRole.USER)
                    .isActive(true)
                    .build());

            userToken = jwtProvider.generateToken(testUser.getId(), testUser.getRole().name());
            return null;
        });
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
                // Then (1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("newnickname"));

        // Then (2): DB 직접 확인
        em.flush();
        em.clear();
        UserJpaEntity updatedUser = userJpaRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo("newnickname");
    }

    @Test
    @DisplayName("사용자 닉네임 수정 실패 - 중복된 닉네임")
    void updateNicknameFailDuplicated() throws Exception {
        // Given: 다른 사용자 존재
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
                // Then (1)
                .andExpect(status().isOk());

        // Then (2): DB 직접 확인 (기존 비밀번호와 달라야 함)
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
                // Then (1)
                .andExpect(status().isOk());

        // Then (2): DB 직접 확인 (비활성화 상태여야 함)
        em.flush();
        em.clear();
        UserJpaEntity deactivatedUser = userJpaRepository.findById(testUser.getId()).orElseThrow();
        assertThat(deactivatedUser.isActive()).isFalse();
        assertThat(deactivatedUser.getDeactivatedAt()).isNotNull();
    }

    @Test
    @DisplayName("보안 테스트 - 인증 토큰 없이 요청 시 실패")
    void securityFailNoToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("보안 테스트 - 잘못된 토큰으로 요청 시 실패")
    void securityFailInvalidToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(AuthResponseCode.UNAUTHORIZED.getMessage()));
    }

    @Autowired
    private com.kthowns.mobidic.api.security.jwt.JwtProperties jwtProperties;

    @Test
    @DisplayName("보안 테스트 - 만료된 토큰으로 요청 시 실패")
    void securityFailExpiredToken() throws Exception {
        // Given: 만료된 토큰 직접 생성
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .subject(testUser.getId().toString())
                .claim("role", testUser.getRole().name())
                .issuedAt(new java.util.Date(System.currentTimeMillis() - 100000))
                .expiration(new java.util.Date(System.currentTimeMillis() - 50000))
                .signWith(jwtProperties.getSecretKey())
                .compact();

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
        // 프로덕션 코드에서 만료된 토큰에 대한 구체적인 에러 메시지가 다를 수 있으므로 상태 코드만 검증
    }

    @Test
    @DisplayName("보안 테스트 - 탈퇴한(비활성화된) 사용자 토큰으로 요청 시 실패")
    void securityFailDeactivatedUser() throws Exception {
        // Given: 사용자 비활성화 및 블랙리스트 등록 시뮬레이션
        transactionTemplate.execute(status -> {
            UserJpaEntity user = userJpaRepository.findById(testUser.getId()).orElseThrow();

            // DB 상태 변경 (Soft Delete)
            em.createQuery("update UserJpaEntity u set u.isActive = false where u.id = :id")
                    .setParameter("id", user.getId())
                    .executeUpdate();

            return null;
        });
        em.clear();

        // Mock 설정: 해당 유저 ID 조회 시 블랙리스트에 있다고 가정
        given(userBlackListService.isDeactivatedUser(testUser.getId())).willReturn(true);

        // When & Then: 비활성화된 사용자의 토큰으로 API 호출
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isUnauthorized());
    }
}
