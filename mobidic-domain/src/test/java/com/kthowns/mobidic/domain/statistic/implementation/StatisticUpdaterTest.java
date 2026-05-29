package com.kthowns.mobidic.domain.statistic.implementation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatisticUpdaterTest {

    @Mock
    private WordStatisticRepository wordStatisticRepository;

    @InjectMocks
    private StatisticUpdater target;

    @Test
    @DisplayName("toggleLearned 테스트 - 성공")
    void toggleLearnedTest_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        WordStatistic existingStat = new WordStatistic(wordId, 0, 0, false, 0.0, 0.0);
        given(wordStatisticRepository.readForUpdate(wordId, userId)).willReturn(Optional.of(existingStat));

        // When
        target.toggleLearned(userId, wordId);

        // Then
        ArgumentCaptor<WordStatistic> captor = ArgumentCaptor.forClass(WordStatistic.class);
        verify(wordStatisticRepository).update(captor.capture());
        assertThat(captor.getValue().isLearned()).isTrue();
    }

    @Test
    @DisplayName("toggleLearned 테스트 - 실패 (통계 없음)")
    void toggleLearnedTest_Fail() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        given(wordStatisticRepository.readForUpdate(wordId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> target.toggleLearned(userId, wordId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_STAT.getMessage());
    }

    @Test
    @DisplayName("increaseCorrectCount 테스트 - 성공")
    void increaseCorrectCountTest_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        WordStatistic existingStat = new WordStatistic(wordId, 0, 0, false, 0.0, 0.0);
        given(wordStatisticRepository.readForUpdate(wordId, userId)).willReturn(Optional.of(existingStat));

        // When
        target.increaseCorrectCount(userId, wordId);

        // Then
        ArgumentCaptor<WordStatistic> captor = ArgumentCaptor.forClass(WordStatistic.class);
        verify(wordStatisticRepository).update(captor.capture());
        assertThat(captor.getValue().correctCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("increaseCorrectCount 테스트 - 실패 (통계 없음)")
    void increaseCorrectCountTest_Fail() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        given(wordStatisticRepository.readForUpdate(wordId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> target.increaseCorrectCount(userId, wordId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_STAT.getMessage());
    }

    @Test
    @DisplayName("increaseIncorrectCount 테스트 - 성공")
    void increaseIncorrectCountTest_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        WordStatistic existingStat = new WordStatistic(wordId, 0, 0, false, 0.0, 0.0);
        given(wordStatisticRepository.readForUpdate(wordId, userId)).willReturn(Optional.of(existingStat));

        // When
        target.increaseIncorrectCount(userId, wordId);

        // Then
        ArgumentCaptor<WordStatistic> captor = ArgumentCaptor.forClass(WordStatistic.class);
        verify(wordStatisticRepository).update(captor.capture());
        assertThat(captor.getValue().incorrectCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("increaseIncorrectCount 테스트 - 실패 (통계 없음)")
    void increaseIncorrectCountTest_Fail() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID wordId = UUID.randomUUID();
        given(wordStatisticRepository.readForUpdate(wordId, userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> target.increaseIncorrectCount(userId, wordId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(GeneralResponseCode.NO_STAT.getMessage());
    }
}
