package com.kthowns.mobidic.domain.user.facade;

import com.kthowns.mobidic.domain.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.domain.term.service.TermService;
import com.kthowns.mobidic.api.user.dto.request.SignUpRequestDto;
import com.kthowns.mobidic.storage.user.jpaentity.User;
import com.kthowns.mobidic.domain.user.service.UserService;
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
