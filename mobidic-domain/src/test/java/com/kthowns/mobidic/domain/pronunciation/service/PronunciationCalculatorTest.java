package com.kthowns.mobidic.domain.pronunciation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

@ExtendWith(MockitoExtension.class)
class PronunciationCalculatorTest {

    @InjectMocks
    private PronunciationCalculator pronunciationCalculator;

    @Test
    @DisplayName("calculateSimilarity 테스트 - 완벽히 동일한 경우 (대소문자 무시, 구두점 제거)")
    void calculateSimilarityTest_ExactMatch() {
        // Given
        String expression = "hello world";
        String transcribedText = "hello world."; // The '.' will be removed

        // When
        double similarity = pronunciationCalculator.calculateSimilarity(expression, transcribedText);

        // Then
        assertThat(similarity).isEqualTo(1.0, offset(0.0001));
    }

    @Test
    @DisplayName("calculateSimilarity 테스트 - 부분적으로 일치하는 경우")
    void calculateSimilarityTest_PartialMatch() {
        // Given
        String expression = "apple";
        String transcribedText = "aple";

        // When
        double similarity = pronunciationCalculator.calculateSimilarity(expression, transcribedText);

        // Then
        // Levenshtein 거리는 1 (p 누락). 최대 길이는 5. (5-1)/5 = 0.8
        assertThat(similarity).isEqualTo(0.8, offset(0.0001));
    }

    @Test
    @DisplayName("calculateSimilarity 테스트 - 전혀 일치하지 않는 경우")
    void calculateSimilarityTest_NoMatch() {
        // Given
        String expression = "apple";
        String transcribedText = "zzzzz";

        // When
        double similarity = pronunciationCalculator.calculateSimilarity(expression, transcribedText);

        // Then
        // Levenshtein 거리는 5. (5-5)/5 = 0.0
        assertThat(similarity).isEqualTo(0.0, offset(0.0001));
    }

    @Test
    @DisplayName("calculateSimilarity 테스트 - 빈 문자열인 경우")
    void calculateSimilarityTest_EmptyString() {
        // Given
        String expression = "";
        String transcribedText = "a";

        // When
        double similarity = pronunciationCalculator.calculateSimilarity(expression, transcribedText);

        // Then
        assertThat(similarity).isEqualTo(0.0, offset(0.0001));
    }

    @Test
    @DisplayName("calculateSimilarity 테스트 - 둘 다 빈 문자열인 경우")
    void calculateSimilarityTest_BothEmpty() {
        // Given
        String expression = "";
        String transcribedText = "";

        // When
        double similarity = pronunciationCalculator.calculateSimilarity(expression, transcribedText);

        // Then
        assertThat(similarity).isEqualTo(0.0, offset(0.0001));
    }
}
