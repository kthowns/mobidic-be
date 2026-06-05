package com.kthowns.mobidic.api.integration;

import com.kthowns.mobidic.api.security.util.JwtProvider;
import com.kthowns.mobidic.domain.auth.repository.AuthRedisKey;
import com.kthowns.mobidic.domain.user.model.UserRole;
import com.kthowns.mobidic.storage.user.jpaentity.UserJpaEntity;
import com.kthowns.mobidic.storage.user.jparepository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BlackListFallbackIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private jakarta.persistence.EntityManager em;

    private UserJpaEntity activeUser;
    private UserJpaEntity deactivatedUser;
    private String activeUserToken;
    private String deactivatedUserToken;

    @BeforeEach
    void setup() {
        activeUser = userJpaRepository.save(UserJpaEntity.builder()
                .email("active@test.com")
                .nickname("active")
                .password("pass")
                .role(UserRole.USER)
                .isActive(true)
                .build());

        deactivatedUser = userJpaRepository.save(UserJpaEntity.builder()
                .email("deactivated@test.com")
                .nickname("deactivated")
                .password("pass")
                .role(UserRole.USER)
                .isActive(false)
                .build());

        activeUserToken = jwtProvider.generateToken(activeUser.getId(), activeUser.getRole().name());
        deactivatedUserToken = jwtProvider.generateToken(deactivatedUser.getId(), deactivatedUser.getRole().name());

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Redis 장애 시 DB Fallback 작동 확인 - 활성 사용자 통과")
    void fallbackWithActiveUser() throws Exception {
        // Given
        String key = AuthRedisKey.DEACTIVATED + ":" + activeUser.getId();
        given(redisTemplate.hasKey(key)).willThrow(new RedisConnectionFailureException("Redis is down"));

        // When
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + activeUserToken))
                // Then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Redis 장애 시 DB Fallback 작동 확인 - 비활성 사용자 차단")
    void fallbackWithDeactivatedUser() throws Exception {
        // Given
        String key = AuthRedisKey.DEACTIVATED + ":" + deactivatedUser.getId();
        given(redisTemplate.hasKey(key)).willThrow(new RedisConnectionFailureException("Redis is down"));

        // When
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + deactivatedUserToken))
                // Then
                .andExpect(status().isUnauthorized());
    }
}
