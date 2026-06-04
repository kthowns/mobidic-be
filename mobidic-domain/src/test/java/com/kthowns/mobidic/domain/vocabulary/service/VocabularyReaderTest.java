package com.kthowns.mobidic.domain.vocabulary.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.vocabulary.model.Vocabulary;
import com.kthowns.mobidic.domain.vocabulary.model.VocabularyDetail;
import com.kthowns.mobidic.domain.vocabulary.repository.VocabularyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VocabularyReaderTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyReader vocabularyReader;

    @Test
    @DisplayName("readDetailsByUserId 테스트 - 상세 목록 조회 성공")
    void readDetailsByUserIdTest() {
        // Given
        UUID userId = UUID.randomUUID();
        VocabularyDetail detail = new VocabularyDetail(new Vocabulary(UUID.randomUUID(), userId, "title", "desc", 0, null), 0.0, 0.0);
        List<VocabularyDetail> expectedDetails = List.of(detail);
        given(vocabularyRepository.readDetailsByUserId(userId)).willReturn(expectedDetails);

        // When
        List<VocabularyDetail> actualDetails = vocabularyReader.readDetailsByUserId(userId);

        // Then
        assertThat(actualDetails).isEqualTo(expectedDetails);
    }

    @Test
    @DisplayName("readById 테스트 - ID로 조회 성공")
    void readByIdTest_Success() {
        // Given
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Vocabulary expectedVocab = new Vocabulary(vocabId, userId, "title", "desc", 0, null);
        given(vocabularyRepository.readByIdAndUserId(vocabId, userId)).willReturn(Optional.of(expectedVocab));

        // When
        Vocabulary actualVocab = vocabularyReader.readById(vocabId, userId);

        // Then
        assertThat(actualVocab).isEqualTo(expectedVocab);
    }

    @Test
    @DisplayName("readById 테스트 - 조회 실패 (예외 발생)")
    void readByIdTest_Fail() {
        // Given
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.readByIdAndUserId(vocabId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vocabularyReader.readById(vocabId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_VOCAB.getMessage());
    }

    @Test
    @DisplayName("readDetailById 테스트 - 상세 정보 조회 성공")
    void readDetailByIdTest_Success() {
        // Given
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        VocabularyDetail expectedDetail = new VocabularyDetail(new Vocabulary(vocabId, userId, "title", "desc", 0, null), 0.0, 0.0);
        given(vocabularyRepository.readDetailById(vocabId, userId)).willReturn(Optional.of(expectedDetail));

        // When
        VocabularyDetail actualDetail = vocabularyReader.readDetailById(userId, vocabId);

        // Then
        assertThat(actualDetail).isEqualTo(expectedDetail);
    }

    @Test
    @DisplayName("readDetailById 테스트 - 조회 실패 (예외 발생)")
    void readDetailByIdTest_Fail() {
        // Given
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.readDetailById(vocabId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vocabularyReader.readDetailById(userId, vocabId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_VOCAB.getMessage());
    }

    @Test
    @DisplayName("existsByUser 테스트")
    void existsByUserTest() {
        // Given
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.existsByUserId(userId)).willReturn(true);

        // When
        boolean exists = vocabularyReader.existsByUser(userId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByIdAndUser 테스트")
    void existsByIdAndUserTest() {
        // Given
        UUID vocabId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(vocabularyRepository.existsByIdAndUser_Id(vocabId, userId)).willReturn(true);

        // When
        boolean exists = vocabularyReader.existsByIdAndUser(vocabId, userId);

        // Then
        assertThat(exists).isTrue();
    }
}
