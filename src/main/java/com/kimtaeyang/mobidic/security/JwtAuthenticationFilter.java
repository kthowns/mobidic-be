package com.kimtaeyang.mobidic.security;

import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.exception.AuthAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;
    private final AuthAuthenticationEntryPoint authAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //JWT 파싱
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // "Bearer " 제거

        try{
            if (!jwtUtil.validateToken(token)
                    || jwtBlacklistService.isTokenLogout(token)
                    || jwtBlacklistService.isTokenWithdrawn(token)
            ) {
                throw new BadCredentialsException("Invalid token");
            }
        } catch (AuthenticationException e){
            SecurityContextHolder.clearContext();
            authAuthenticationEntryPoint.commence(request, response, e);
            return;
        }

        //JWT Claim 에 포함된 정보만 갖는 Member 객체
        UUID id = jwtUtil.getIdFromToken(token);
        Member claim = Member.builder()
                .id(id)
                .build();

        //Security Context에 사용자 UUID 저장
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(claim, null, claim.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
