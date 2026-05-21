package com.kthowns.mobidic.domain.quiz.client;

public interface CryptoClient {
    String encrypt(String plainText);
    String decrypt(String cipherText);
}
