package com.kthowns.mobidic.domain.quiz.implementation;

import com.kthowns.mobidic.domain.quiz.client.CryptoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizProcessor {
    private final CryptoClient cryptoClient;

    public String encryptKey(String key) {
        return cryptoClient.encrypt(key);
    }

    public String decryptKey(String token) {
        return cryptoClient.decrypt(token);
    }
}
