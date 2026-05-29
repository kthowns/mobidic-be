package com.kthowns.mobidic.domain.term.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.term.model.SimpleTerm;
import com.kthowns.mobidic.domain.term.model.Term;
import com.kthowns.mobidic.domain.term.model.TermType;
import com.kthowns.mobidic.domain.term.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TermReaderTest {

    @Mock
    private TermRepository termRepository;

    @InjectMocks
    private TermReader target;

    @Test
    @DisplayName("readTerm 테스트 - 특정 버전 조회 성공")
    void readTermTest_WithVersion() {
        // Given
        TermType type = TermType.SERVICE;
        String version = "1.0.0";
        Term expectedTerm = new Term(1L, type, version, true, "내용", LocalDateTime.now());
        given(termRepository.readByTypeAndVersion(type, version)).willReturn(Optional.of(expectedTerm));

        // When
        Term actualTerm = target.readTerm(type, version);

        // Then
        assertThat(actualTerm).isEqualTo(expectedTerm);
    }

    @Test
    @DisplayName("readTerm 테스트 - 최신 버전 조회 성공 (버전 null)")
    void readTermTest_NullVersion() {
        // Given
        TermType type = TermType.SERVICE;
        Term expectedTerm = new Term(1L, type, "2.0.0", true, "최신 내용", LocalDateTime.now());
        given(termRepository.readLatestByType(type)).willReturn(Optional.of(expectedTerm));

        // When
        Term actualTerm = target.readTerm(type, null);

        // Then
        assertThat(actualTerm).isEqualTo(expectedTerm);
    }

    @Test
    @DisplayName("readTerm 테스트 - 조회 실패 (예외)")
    void readTermTest_Fail() {
        // Given
        TermType type = TermType.SERVICE;
        String version = "9.9.9";
        given(termRepository.readByTypeAndVersion(type, version)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> target.readTerm(type, version))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_TERM.getMessage());
    }

    @Test
    @DisplayName("readActiveTerms 테스트 - 활성 약관 목록 조회")
    void readActiveTermsTest() {
        // Given
        List<SimpleTerm> expectedTerms = List.of(
                new SimpleTerm(1L, TermType.SERVICE, "1.0.0", true, "uri", LocalDateTime.now()),
                new SimpleTerm(2L, TermType.PRIVACY, "1.0.0", false, "uri", LocalDateTime.now())
        );
        given(termRepository.readActiveTerms()).willReturn(expectedTerms);

        // When
        List<SimpleTerm> actualTerms = target.readActiveTerms();

        // Then
        assertThat(actualTerms).isEqualTo(expectedTerms);
    }
}
