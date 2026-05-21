package com.kthowns.mobidic.domain.pronunciation.client;

import org.springframework.web.multipart.MultipartFile;

public interface SpeechToTextClient {
    String transcribe(MultipartFile file);
}
