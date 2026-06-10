package com.kthowns.mobidic.domain.user.client;

public interface PasswordEncoderClient {
    String encode(String rawPassword);
}
