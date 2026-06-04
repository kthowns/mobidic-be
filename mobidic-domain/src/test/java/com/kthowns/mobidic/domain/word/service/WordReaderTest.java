package com.kthowns.mobidic.domain.word.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import com.kthowns.mobidic.domain.word.repository.WordRepository;
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
class WordReaderTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordReader wordReader;

    @Test
    @DisplayName("readDetailsByVocabularyId 테스트 - 상세 목록 조회 성공")
    void readDetailsByVocabularyIdTest() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID vocabularyId = UUID.randomUUID();
        WordDetail detail = new WordDetail(UUID.randomUUID(), "expression", 0.0, 0.0, false, List.of(), null);
        List<WordDetail> expectedDetails = List.of(detail);
        given(wordRepository.readDetailsByVocabularyId(userId, vocabularyId, false)).willReturn(expectedDetails);

        // When
        List<WordDetail> actualDetails = wordReader.readDetailsByVocabularyId(userId, vocabularyId, false);

        // Then
        assertThat(actualDetails).isEqualTo(expectedDetails);
    }

    @Test
    @DisplayName("readByIdAndUserId 테스트 - 조회 성공")
    void readByIdAndUserIdTest_Success() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Word expectedWord = new Word(wordId, UUID.randomUUID(), "expression", null);
        given(wordRepository.readByIdAndUserId(wordId, userId)).willReturn(Optional.of(expectedWord));

        // When
        Word actualWord = wordReader.readByIdAndUserId(wordId, userId);

        // Then
        assertThat(actualWord).isEqualTo(expectedWord);
    }

    @Test
    @DisplayName("readByIdAndUserId 테스트 - 조회 실패 (예외 발생)")
    void readByIdAndUserIdTest_Fail() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordRepository.readByIdAndUserId(wordId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> wordReader.readByIdAndUserId(wordId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_WORD.getMessage());
    }
}
