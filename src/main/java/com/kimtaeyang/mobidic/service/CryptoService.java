package com.kimtaeyang.mobidic.service;

import jakarta.annotation.PostConstruct;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {
    private final AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
    @Value("${jasypt.encryptor.password}")
    private String password;

    @PostConstruct
    public void init() {
        textEncryptor.setPassword(password);
    }

    public String encrypt(String plainText) {
        return textEncryptor.encrypt(plainText);
    }

    public String decrypt(String cipherText) {
        return textEncryptor.decrypt(cipherText);
    }
}
