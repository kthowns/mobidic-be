package com.kimtaeyang.mobidic.service;

import com.kimtaeyang.mobidic.dto.member.MemberDto;
import com.kimtaeyang.mobidic.dto.member.UpdateNicknameRequestDto;
import com.kimtaeyang.mobidic.dto.member.UpdatePasswordRequestDto;
import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.exception.ApiException;
import com.kimtaeyang.mobidic.repository.MemberRepository;
import com.kimtaeyang.mobidic.security.JwtBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.kimtaeyang.mobidic.code.AuthResponseCode.NO_MEMBER;
import static com.kimtaeyang.mobidic.code.GeneralResponseCode.DUPLICATED_NICKNAME;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    @PreAuthorize("@memberAccessHandler.ownershipCheck(#memberId)")
    public MemberDto getMemberDetailById(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_MEMBER));

        return MemberDto.fromEntity(member);
    }

    @Transactional
    @PreAuthorize("@memberAccessHandler.ownershipCheck(#memberId)")
    public MemberDto updateMemberNickname(
            UUID memberId, UpdateNicknameRequestDto request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_MEMBER));

        int count = memberRepository.countByNicknameAndIdNot(request.getNickname(), memberId);

        if (count > 0) {
            throw new ApiException(DUPLICATED_NICKNAME);
        }

        member.setNickname(request.getNickname());
        member = memberRepository.save(member);

        return MemberDto.fromEntity(member);
    }

    @Transactional
    @PreAuthorize("@memberAccessHandler.ownershipCheck(#memberId)")
    public MemberDto updateMemberPassword(
            UUID memberId, UpdatePasswordRequestDto request, String token
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_MEMBER));

        member.setPassword(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);

        authService.logout(memberId, token);

        return MemberDto.fromEntity(member);
    }

    @Transactional
    @PreAuthorize("@memberAccessHandler.ownershipCheck(#memberId)")
    public MemberDto withdrawMember(String token, UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_MEMBER));

        member.setWithdrawnAt(Timestamp.valueOf(LocalDateTime.now()));
        member.setIsActive(false);
        memberRepository.save(member);

        jwtBlacklistService.withdrawToken(token);
        SecurityContextHolder.clearContext();

        return MemberDto.fromEntity(member);
    }

    @Transactional
    @PreAuthorize("@memberAccessHandler.ownershipCheck(#memberId)")
    public MemberDto deleteMember(String token, UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(NO_MEMBER));
        memberRepository.deleteById(memberId);

        jwtBlacklistService.withdrawToken(token);
        SecurityContextHolder.clearContext();

        return MemberDto.fromEntity(member);
    }
}
