package com.kthowns.mobidic.domain.term.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TermValidatorTest {

    @Mock
    private TermRepository termRepository;

    @InjectMocks
    private TermValidator target;

    @Test
    @DisplayName("validateVersionDuplication 테스트 - 중복 없음 (통과)")
    void validateVersionDuplicationTest_Success() {
        // Given
        TermType type = TermType.SERVICE;
        String version = "1.0.0";
        given(termRepository.existsByTypeAndVersion(type, version)).willReturn(false);

        // When & Then
        assertThatCode(() -> target.validateVersionDuplication(type, version))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateVersionDuplication 테스트 - 중복 발생 (예외)")
    void validateVersionDuplicationTest_Fail() {
        // Given
        TermType type = TermType.SERVICE;
        String version = "1.0.0";
        given(termRepository.existsByTypeAndVersion(type, version)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> target.validateVersionDuplication(type, version))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.DUPLICATED_TERM_VERSION.getMessage());
    }

    @Test
    @DisplayName("validateAgreement 테스트 - 유효한 동의 (통과)")
    void validateAgreementTest_Success() {
        // Given
        List<Long> requiredIds = List.of(1L, 2L);
        List<Long> agreeTermIds = List.of(1L, 2L, 3L);
        given(termRepository.readAllRequiredTermIds()).willReturn(requiredIds);
        given(termRepository.countByIds(agreeTermIds)).willReturn(3L); // 모든 ID가 유효함

        // When & Then
        assertThatCode(() -> target.validateAgreement(agreeTermIds))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateAgreement 테스트 - 필수 약관 미동의 (예외)")
    void validateAgreementTest_Fail_RequiredNotAgreed() {
        // Given
        List<Long> requiredIds = List.of(1L, 2L);
        List<Long> agreeTermIds = List.of(1L); // 2L 누락
        given(termRepository.readAllRequiredTermIds()).willReturn(requiredIds);

        // When & Then
        assertThatThrownBy(() -> target.validateAgreement(agreeTermIds))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.REQUIRED_TERM_NOT_AGREED.getMessage());
    }

    @Test
    @DisplayName("validateAgreement 테스트 - 존재하지 않는 약관 ID 포함 (예외)")
    void validateAgreementTest_Fail_InvalidTermId() {
        // Given
        List<Long> requiredIds = List.of(1L);
        List<Long> agreeTermIds = List.of(1L, 999L); // 999L은 존재하지 않는다고 가정
        given(termRepository.readAllRequiredTermIds()).willReturn(requiredIds);
        given(termRepository.countByIds(agreeTermIds)).willReturn(1L); // 1개만 유효함

        // When & Then
        assertThatThrownBy(() -> target.validateAgreement(agreeTermIds))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.INVALID_TERM_ID_INCLUDED.getMessage());
    }
}
