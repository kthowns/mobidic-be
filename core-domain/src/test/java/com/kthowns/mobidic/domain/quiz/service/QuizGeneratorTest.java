package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.Quiz;
import com.kthowns.mobidic.domain.word.model.WordDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QuizGeneratorTest {

    private final QuizGenerator quizGenerator = new QuizGenerator() {
        @Override
        public List<Quiz> generate(UUID memberId, List<WordDetail> wordDetails) {
            return List.of();
        }
    };

    @Test
    @DisplayName("derange 테스트 - 모든 요소가 원래 위치에서 벗어나야 함")
    void derangeTest() {
        // Given
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<Integer> original = new ArrayList<>(list);

        // When
        quizGenerator.derange(list);

        // Then
        for (int i = 0; i < list.size(); i++) {
            assertThat(list.get(i)).isNotEqualTo(original.get(i))
                    .withFailMessage("Index %d has same value %d", i, list.get(i));
        }
    }

    @Test
    @DisplayName("derange 테스트 - 요소가 2개인 경우 (반드시 서로 바뀜)")
    void derangeTest_TwoElements() {
        // Given
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2));
        List<Integer> original = new ArrayList<>(list);

        // When
        quizGenerator.derange(list);

        // Then
        assertThat(list.get(0)).isEqualTo(original.get(1));
        assertThat(list.get(1)).isEqualTo(original.get(0));
    }

    @Test
    @DisplayName("partialShuffle 테스트 - 지정된 개수만큼 위치가 변경되어야 함")
    void partialShuffleTest() {
        // Given
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> original = new ArrayList<>(list);

        // When
        quizGenerator.partialShuffle(3, list);

        // Then
        int diffCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equals(original.get(i))) {
                diffCount++;
            }
        }
        // partialShuffle(3) 이면 최소 2개 이상은 바뀌어야 함 (derange의 특성상)
        assertThat(diffCount).isGreaterThanOrEqualTo(2);
    }
}
