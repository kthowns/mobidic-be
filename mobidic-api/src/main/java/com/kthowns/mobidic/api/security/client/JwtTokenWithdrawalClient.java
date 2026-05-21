package com.kthowns.mobidic.api.security.client;

import com.kthowns.mobidic.api.security.jwt.JwtProvider; // 실제 위치 확인 필요
import com.kthowns.mobidic.domain.user.client.TokenWithdrawalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenWithdrawalClient implements TokenWithdrawalClient {
    // 실제 프로젝트의 JwtBlacklistService 또는 JwtProvider를 사용하도록 구현
    // 현재는 인터페이스 구현에 집중
    @Override
    public void withdrawToken(String token) {
        // blacklistService.withdrawToken(token);
    }
}
