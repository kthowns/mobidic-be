package com.kthowns.mobidic.domain.user.implementation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import com.kthowns.mobidic.domain.definition.repository.*;
import com.kthowns.mobidic.domain.preset.repository.*;
import com.kthowns.mobidic.domain.quiz.repository.*;
import com.kthowns.mobidic.domain.statistic.repository.*;
import com.kthowns.mobidic.domain.term.repository.*;
import com.kthowns.mobidic.domain.user.repository.*;
import com.kthowns.mobidic.domain.vocabulary.repository.*;
import com.kthowns.mobidic.domain.word.repository.*;
import com.kthowns.mobidic.domain.user.client.*;
import com.kthowns.mobidic.domain.pronunciation.client.*;
import com.kthowns.mobidic.domain.quiz.client.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator target;

    @Test
    @DisplayName("validateEmailDuplication 테스트")
    void validateEmailDuplicationTest() {
        // Given
        // When
        // Then
    }

    @Test
    @DisplayName("validateNicknameDuplication 테스트")
    void validateNicknameDuplicationTest() {
        // Given
        // When
        // Then
    }

    @Test
    @DisplayName("validateNicknameUpdateDuplication 테스트")
    void validateNicknameUpdateDuplicationTest() {
        // Given
        // When
        // Then
    }

    @Test
    @DisplayName("validatePassword 테스트")
    void validatePasswordTest() {
        // Given
        // When
        // Then
    }

}
