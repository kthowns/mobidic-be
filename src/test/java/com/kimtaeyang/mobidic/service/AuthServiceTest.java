package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.config.JwtProperties;
import com.kimtaeyang.mobidic.dto.member.JoinRequestDto;
import com.kimtaeyang.mobidic.dto.member.LoginDto;
import com.kimtaeyang.mobidic.dto.member.MemberDto;
import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import com.kimtaeyang.mobidic.security.JwtBlacklistService;
import com.kimtaeyang.mobidic.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "jwt.secret=qwerqwerqwerqwerqwerqwerqwerqwer",
        "jwt.exp=3600"
})
@ContextConfiguration(classes = {AuthService.class, AuthServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository; // mock

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("[AuthService] Join success")
    void joinTestSuccess() {
        // given
        String rawPassword = "test1234";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        JoinRequestDto request = JoinRequestDto.builder()
                .email("user@example.com")
                .nickname("tester")
                .password(rawPassword)
                .build();

        Member memberToReturn = Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(encodedPassword)
                .build();

        // mocking
        Mockito.when(memberRepository.countByNickname(anyString()))
                .thenReturn(0);
        Mockito.when(memberRepository.countByEmail(anyString()))
                .thenReturn(0);
        Mockito.when(memberRepository.save(Mockito.any(Member.class)))
                .thenReturn(memberToReturn);

        // when
        MemberDto response = authService.join(request);

        // then
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getNickname(), response.getNickname());
        Mockito.verify(memberRepository).save(Mockito.any(Member.class));
    }

    @Test
    @DisplayName("[AuthService] Login success")
    void loginTestSuccess() {
        String rawPassword = "test1234";

        LoginDto.Request request = LoginDto.Request.builder()
                .email("user@example.com")
                .password(rawPassword)
                .build();

        Member principal = Member.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .build();

        Authentication mockAuth = Mockito.mock(Authentication.class);

        // given
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        Mockito.when(mockAuth.getPrincipal())
                .thenReturn(principal);

        // when
        String token = authService.login(request).getToken();

        // then
        assertEquals(principal.getId(), jwtUtil.getIdFromToken(token));
        assertThat(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("[AuthService] Login failed")
    void loginTestFail() {
        String rawPassword = "test1234";

        LoginDto.Request request = LoginDto.Request.builder()
                .email("user@example.com")
                .password(rawPassword)
                .build();

        // given
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        //when
        Throwable e = assertThrows(Exception.class, () -> authService.login(request));

        // then
        assertEquals(e.getMessage(), e.getMessage());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MemberRepository memberRepository() {
            return Mockito.mock(MemberRepository.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(); // 실제 컴포넌트 사용
        }

        @Bean
        public JwtProperties jwtProperties() {
            return new JwtProperties();
        }

        @Bean
        public JwtUtil jwtUtil() {
            return new JwtUtil(jwtProperties());
        }

        @Bean
        public JwtBlacklistService jwtBlacklistService() {
            return Mockito.mock(JwtBlacklistService.class);
        }

        @Bean
        public AuthenticationManager authenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
        }
    }
}
