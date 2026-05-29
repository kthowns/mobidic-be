package com.kthowns.mobidic.domain.vocabulary.implementation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import com.kthowns.mobidic.domain.definition.repository.*;
import com.kthowns.mobidic.domain.preset.repository.*;
import com.kthowns.mobidic.domain.quiz.repository.*;
import com.kthowns.mobidic.domain.statistic.repository.*;
import com.kthowns.mobidic.domain.term.repository.*;
import com.kthowns.mobidic.domain.user.repository.*;
import com.kthowns.mobidic.domain.vocabulary.repository.*;
import com.kthowns.mobidic.domain.word.repository.*;
import com.kthowns.mobidic.domain.user.client.*;
import com.kthowns.mobidic.domain.pronunciation.client.*;
import com.kthowns.mobidic.domain.quiz.client.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VocabularyReaderTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyReader target;

    @Test
    @DisplayName("readDetailsByUserId 테스트")
    void readDetailsByUserIdTest() {
        // Given
        // When
        // Then
    }

    @Test
    @DisplayName("readById 테스트")
    void readByIdTest() {
        // Given
        // When
        // Then
    }

    @Test
    @DisplayName("readDetailById 테스트")
    void readDetailByIdTest() {
        // Given
        // When
        // Then
    }

    @Test
    @DisplayName("existsByUser 테스트")
    void existsByUserTest() {
        // Given
        // When
        // Then
    }

    @Test
    @DisplayName("existsByIdAndUser 테스트")
    void existsByIdAndUserTest() {
        // Given
        // When
        // Then
    }

}
