package com.kthowns.mobidic.domain.statistic.service;

import com.kthowns.mobidic.domain.statistic.model.WordStatistic;
import com.kthowns.mobidic.domain.statistic.repository.WordStatisticRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatisticAppenderTest {

    @Mock
    private WordStatisticRepository wordStatisticRepository;

    @InjectMocks
    private StatisticAppender statisticAppender;

    @Test
    @DisplayName("append 테스트 - 통계 생성 성공")
    void appendTest() {
        // Given
        UUID wordId = UUID.randomUUID();

        // When
        statisticAppender.append(wordId);

        // Then
        verify(wordStatisticRepository).append(any(WordStatistic.class));
    }
}
