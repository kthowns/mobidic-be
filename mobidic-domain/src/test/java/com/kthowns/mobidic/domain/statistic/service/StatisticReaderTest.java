package com.kthowns.mobidic.domain.statistic.service;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
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
class StatisticReaderTest {

    @Mock
    private WordStatisticRepository wordStatisticRepository;

    @InjectMocks
    private StatisticReader statisticReader;

    @Test
    @DisplayName("readByWordIdAndUserId 테스트 - 조회 성공")
    void readByWordIdAndUserIdTest_Success() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        WordStatistic expectedStat = new WordStatistic(wordId, 5, 2, true, 0.5, 0.8);
        given(wordStatisticRepository.readByWordIdAndUserId(wordId, userId)).willReturn(Optional.of(expectedStat));

        // When
        WordStatistic actualStat = statisticReader.readByWordIdAndUserId(wordId, userId);

        // Then
        assertThat(actualStat).isEqualTo(expectedStat);
    }

    @Test
    @DisplayName("readByWordIdAndUserId 테스트 - 조회 실패 (예외 발생)")
    void readByWordIdAndUserIdTest_Fail() {
        // Given
        UUID wordId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordStatisticRepository.readByWordIdAndUserId(wordId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> statisticReader.readByWordIdAndUserId(wordId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_STAT.getMessage());
    }

    @Test
    @DisplayName("readByVocabularyId 테스트 - 단어장 ID로 통계 목록 조회 성공")
    void readByVocabularyIdTest() {
        // Given
        UUID vocabularyId = UUID.randomUUID();
        List<WordStatistic> expectedStats = List.of(
                new WordStatistic(UUID.randomUUID(), 1, 0, true, 0.0, 1.0)
        );
        given(wordStatisticRepository.readByVocabularyId(vocabularyId)).willReturn(expectedStats);

        // When
        List<WordStatistic> actualStats = statisticReader.readByVocabularyId(vocabularyId);

        // Then
        assertThat(actualStats).isEqualTo(expectedStats);
    }

    @Test
    @DisplayName("readByUserId 테스트 - 사용자 ID로 통계 목록 조회 성공")
    void readByUserIdTest() {
        // Given
        UUID userId = UUID.randomUUID();
        List<WordStatistic> expectedStats = List.of(
                new WordStatistic(UUID.randomUUID(), 1, 0, true, 0.0, 1.0)
        );
        given(wordStatisticRepository.readByUserId(userId)).willReturn(expectedStats);

        // When
        List<WordStatistic> actualStats = statisticReader.readByUserId(userId);

        // Then
        assertThat(actualStats).isEqualTo(expectedStats);
    }

    @Test
    @DisplayName("readVocabLearningRate 테스트 - 조회 성공")
    void readVocabLearningRateTest_Success() {
        // Given
        UUID vocabularyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordStatisticRepository.calculateVocabularyLearningRate(vocabularyId, userId)).willReturn(Optional.of(0.75));

        // When
        Double result = statisticReader.readVocabLearningRate(vocabularyId, userId);

        // Then
        assertThat(result).isEqualTo(0.75);
    }

    @Test
    @DisplayName("readVocabLearningRate 테스트 - 조회 실패 (예외 발생)")
    void readVocabLearningRateTest_Fail() {
        // Given
        UUID vocabularyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        given(wordStatisticRepository.calculateVocabularyLearningRate(vocabularyId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> statisticReader.readVocabLearningRate(vocabularyId, userId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}
