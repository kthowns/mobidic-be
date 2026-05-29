package com.kthowns.mobidic.domain.term.implementation;

import com.kthowns.mobidic.domain.term.repository.UserAgreementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserAgreementAppenderTest {

    @Mock
    private UserAgreementRepository userAgreementRepository;

    @InjectMocks
    private UserAgreementAppender target;

    @Test
    @DisplayName("appendAgreements 테스트 - 사용자 동의 저장 성공")
    void appendAgreementsTest() {
        // Given
        UUID userId = UUID.randomUUID();
        List<Long> agreeTermIds = List.of(1L, 2L, 3L);

        // When
        target.appendAgreements(userId, agreeTermIds);

        // Then
        verify(userAgreementRepository).appendAgreements(userId, agreeTermIds);
    }
}
