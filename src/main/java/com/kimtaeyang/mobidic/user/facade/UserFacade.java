package com.kimtaeyang.mobidic.user.facade;

import com.kimtaeyang.mobidic.preset.service.PresetVocabularyService;
import com.kimtaeyang.mobidic.term.service.TermService;
import com.kimtaeyang.mobidic.user.dto.SignUpRequestDto;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final TermService termService;
    private final PresetVocabularyService presetVocabularyService;

    @Transactional
    public void signUp(SignUpRequestDto requestDto) {
        termService.validateSignUpAgreement(requestDto.getAgreeTermIds());
        User user = userService.registerUser(requestDto);
        termService.addUserAgreement(user, requestDto.getAgreeTermIds());

        presetVocabularyService.copyAllPresetToUser(user);
    }
}
