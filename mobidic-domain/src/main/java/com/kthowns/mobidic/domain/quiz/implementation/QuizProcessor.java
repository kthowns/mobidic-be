package com.kthowns.mobidic.domain.quiz.implementation;

import com.kthowns.mobidic.domain.quiz.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizProcessor {
    private final CryptoService cryptoService;

    public String encryptKey(String key) {
        return cryptoService.encrypt(key);
    }

    public String decryptKey(String token) {
        return cryptoService.decrypt(token);
    }
}
