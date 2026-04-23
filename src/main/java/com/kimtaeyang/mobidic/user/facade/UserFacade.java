package com.kimtaeyang.mobidic.user.facade;

import com.kimtaeyang.mobidic.auth.dto.LoginResponse;
import com.kimtaeyang.mobidic.auth.service.KakaoAuthService;
import com.kimtaeyang.mobidic.preset.service.PresetVocabularyService;
import com.kimtaeyang.mobidic.term.service.TermService;
import com.kimtaeyang.mobidic.user.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFacade {
    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final PresetVocabularyService presetVocabularyService;
    private final TermService termService;
    private final TransactionTemplate transactionTemplate;

    public LoginResponse kakaoLogin(String authCode, boolean isDev) {
        String accessToken = kakaoAuthService.kakaoLogin(authCode, isDev);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        presetVocabularyService.copyAllPresetToUser(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public void signUp(SignUpRequestDto requestDto) {
        termService.validateSignUpAgreement(requestDto.getAgreeTermIds());
        User savedUser = transactionTemplate.execute((status) -> {
            User user = userService.signUp(requestDto);
            termService.addUserAgreement(user, requestDto.getAgreeTermIds());

            return user;
        });

        if (savedUser != null) {
            presetVocabularyService.copyAllPresetToUser(savedUser);
        }
    }
}
