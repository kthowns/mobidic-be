package com.kthowns.mobidic.domain.pronunciation.service;

import com.kthowns.mobidic.domain.pronunciation.client.SpeechToTextClient;
import com.kthowns.mobidic.domain.pronunciation.implementation.PronunciationCalculator;
import com.kthowns.mobidic.domain.word.model.Word;
import com.kthowns.mobidic.domain.word.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PronunciationService {
    private final WordService wordService;
    private final SpeechToTextClient sttClient;
    private final PronunciationCalculator pronunciationCalculator;

    public Double ratePronunciation(UUID userId, UUID wordId, MultipartFile multipartFile) {
        Word word = wordService.getWordById(userId, wordId);
        String transcribedText = sttClient.transcribe(multipartFile);

        return pronunciationCalculator.calculateSimilarity(word.expression(), transcribedText);
    }
}
