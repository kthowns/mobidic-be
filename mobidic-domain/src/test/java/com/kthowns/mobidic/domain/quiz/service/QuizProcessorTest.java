package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.client.CryptoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QuizProcessorTest {

    @Mock
    private CryptoClient cryptoClient;

    @InjectMocks
    private QuizProcessor quizProcessor;

    @Test
    @DisplayName("encryptKey 테스트 - 키 암호화 성공")
    void encryptKeyTest() {
        // Given
        String key = "quiz:123";
        String encryptedToken = "encrypted_token";
        given(cryptoClient.encrypt(key)).willReturn(encryptedToken);

        // When
        String result = quizProcessor.encryptKey(key);

        // Then
        assertThat(result).isEqualTo(encryptedToken);
    }

    @Test
    @DisplayName("decryptKey 테스트 - 토큰 복호화 성공")
    void decryptKeyTest() {
        // Given
        String token = "encrypted_token";
        String decryptedKey = "quiz:123";
        given(cryptoClient.decrypt(token)).willReturn(decryptedKey);

        // When
        String result = quizProcessor.decryptKey(token);

        // Then
        assertThat(result).isEqualTo(decryptedKey);
    }
}
