package com.kimtaeyang.mobidic.user.facade;

import com.kimtaeyang.mobidic.auth.dto.LoginResponse;
import com.kimtaeyang.mobidic.auth.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.auth.service.KakaoAuthService;
import com.kimtaeyang.mobidic.preset.service.PresetVocabularyService;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFacade {
    final private KakaoAuthService kakaoAuthService;
    final private UserService userService;
    final private PresetVocabularyService presetVocabularyService;

    public LoginResponse kakaoLogin(String authCode, boolean isDev) {
        String accessToken = kakaoAuthService.kakaoLogin(authCode, isDev);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            presetVocabularyService.copyAllPresetToUser(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public void signUp(@Valid SignUpRequestDto requestDto) {
        userService.signUp(requestDto);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            presetVocabularyService.copyAllPresetToUser(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
