package com.kthowns.mobidic.external.quiz;

import com.kthowns.mobidic.domain.quiz.client.CryptoClient;
import jakarta.annotation.PostConstruct;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JasyptCryptoClient implements CryptoClient {
    private final AES256TextEncryptor textEncryptor = new AES256TextEncryptor();

    @Value("${jasypt.encryptor.password}")
    private String password;

    @PostConstruct
    public void init() {
        textEncryptor.setPassword(password);
    }

    @Override
    public String encrypt(String plainText) {
        return textEncryptor.encrypt(plainText);
    }

    @Override
    public String decrypt(String cipherText) {
        return textEncryptor.decrypt(cipherText);
    }
}
