package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.member.JoinRequestDto;
import com.kimtaeyang.mobidic.dto.member.LoginDto;
import com.kimtaeyang.mobidic.dto.member.MemberDto;
import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.exception.ApiException;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import com.kimtaeyang.mobidic.security.JwtBlacklistService;
import com.kimtaeyang.mobidic.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.kimtaeyang.mobidic.code.AuthResponseCode.NO_MEMBER;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.DUPLICATED_EMAIL;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.DUPLICATED_NICKNAME;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;

    @Transactional(readOnly = true)
    public LoginDto.Response login(LoginDto.Request request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Member member = (Member) auth.getPrincipal();

        return LoginDto.Response.builder()
                .memberId(member.getId().toString())
                .token(jwtUtil.generateToken(member.getId()))
                .build();
    }

    @Transactional
    public MemberDto join(@Valid JoinRequestDto request) {
        if (memberRepository.countByNickname(request.getNickname()) > 0) {
            throw new ApiException(DUPLICATED_NICKNAME);
        }

        if (memberRepository.countByEmail(request.getEmail()) > 0) {
            throw new ApiException(DUPLICATED_EMAIL);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return MemberDto.fromEntity(memberRepository.save(member));
    }

    @PreAuthorize("@memberAccessHandler.ownershipCheck(#memberId)")
    public MemberDto logout(UUID memberId, String token) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_MEMBER));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.setAuthenticated(false); //인증 Context 초기화

        jwtBlacklistService.logoutToken(token); //Redis 블랙리스트에 토큰 추가

        return MemberDto.fromEntity(member);
    }
}