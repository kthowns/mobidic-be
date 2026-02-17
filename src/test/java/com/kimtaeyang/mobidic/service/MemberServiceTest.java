package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.member.MemberDto;
import com.kimtaeyang.mobidic.dto.member.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.dto.member.UpdatePasswordRequestDto;
import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import com.kimtaeyang.mobidic.security.JwtBlacklistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestPropertySource(properties = {
        "jwt.secret=qwerqwerqwerqwerqwerqwerqwerqwer",
        "jwt.exp=3600"
})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemberService.class, MemberServiceTest.TestConfig.class})
@ActiveProfiles("dev")
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    private static final String UID = "9f81b0d7-2f8e-4ad3-ae18-41c73dc71b39";

    @Test
    @DisplayName("[MemberService] Get member detail success")
    @WithMockUser(username = UID)
    void getMemberDetailByIdSuccess() {
        resetMock();

        Member member = Member.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("test"))
                .build();

        //given
        given(memberRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(member));

        //when
        MemberDto response = memberService.getMemberDetailById(UUID.fromString(UID));

        //then
        assertEquals(member.getNickname(), response.getNickname());
        assertEquals(member.getEmail(), response.getEmail());
        assertEquals(UUID.fromString(UID), response.getId());
    }

    @Test
    @DisplayName("[MemberService] Update member nickname success")
    @WithMockUser(username = UID)
    void updateMemberNicknameSuccess() {
        resetMock();

        Member defaultMember = Member.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdateNicknameRequestDto request = UpdateNicknameRequestDto.builder()
                .nickname("test2")
                .build();

        ArgumentCaptor<Member> memberCaptor =
                ArgumentCaptor.forClass(Member.class);

        //given
        given(memberRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultMember));
        given(memberRepository.countByNicknameAndIdNot(anyString(), any(UUID.class)))
                .willReturn(0);
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> {
                    Member memberArg = invocation.getArgument(0);
                    memberArg.setNickname(request.getNickname());
                    return memberArg;
                });

        //when
        MemberDto response = memberService.updateMemberNickname(UUID.fromString(UID), request);

        //then
        verify(memberRepository, times(1))
                .save(memberCaptor.capture());

        assertEquals(request.getNickname(), response.getNickname());
        assertEquals(UUID.fromString(UID), response.getId());
    }

    @Test
    @DisplayName("[MemberService] Update member password success")
    @WithMockUser(username = UID)
    void updateMemberPasswordSuccess() {
        resetMock();

        Member defaultMember = Member.builder()
                .id(UUID.fromString(UID))
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("testTest1"))
                .build();

        UpdatePasswordRequestDto request = UpdatePasswordRequestDto.builder()
                .password("testTest2")
                .build();

        ArgumentCaptor<Member> memberCaptor =
                ArgumentCaptor.forClass(Member.class);

        //given
        given(memberRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(defaultMember));
        given(authService.logout(any(UUID.class), anyString()))
                .willReturn(Mockito.mock(MemberDto.class));
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> {
                    Member memberArg = invocation.getArgument(0);
                    memberArg.setPassword(passwordEncoder.encode(
                            request.getPassword()));
                    return memberArg;
                });

        //when
        memberService.updateMemberPassword(UUID.fromString(UID), request, UUID.randomUUID().toString());

        //then
        verify(memberRepository, times(1))
                .save(memberCaptor.capture());
        Member savedMember = memberCaptor.getValue();

        assertThat(passwordEncoder.matches(request.getPassword(), savedMember.getPassword()));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MemberRepository memberRepository() {
            return Mockito.mock(MemberRepository.class);
        }

        @Bean
        public JwtBlacklistService jwtBlacklistService() {
            return Mockito.mock(JwtBlacklistService.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }

    private void resetMock() {
        Mockito.reset(memberRepository);
    }
}