package com.kthowns.mobidic.domain.user.facade;

import com.kthowns.mobidic.domain.preset.service.PresetVocabularyService;
import com.kthowns.mobidic.domain.term.service.TermService;
import com.kthowns.mobidic.domain.user.model.User;
import com.kthowns.mobidic.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final TermService termService;
    private final PresetVocabularyService presetVocabularyService;

    @Transactional
    public void signUp(String email, String nickname, String password, List<Long> agreeTermIds) {
        termService.validateSignUpAgreement(agreeTermIds);
        User user = userService.registerUser(email, nickname, password);
        termService.addUserAgreement(user.id(), agreeTermIds);

        presetVocabularyService.copyAllPresetToUser(user.id());
    }
}
