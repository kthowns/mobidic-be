package com.kthowns.mobidic.api.user.facade;

import com.kthowns.mobidic.api.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.api.term.service.TermService;
import com.kthowns.mobidic.api.dto.request.user.SignUpRequestDto;
import com.kthowns.mobidic.api.user.entity.User;
import com.kthowns.mobidic.api.user.service.UserService;
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
