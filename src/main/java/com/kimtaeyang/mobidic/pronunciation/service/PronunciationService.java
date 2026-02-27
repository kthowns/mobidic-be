package com.kimtaeyang.mobidic.pronunciation.service;

import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.dictionary.entity.Word;
import com.kimtaeyang.mobidic.dictionary.service.WordService;
import com.kimtaeyang.mobidic.pronunciation.dto.SttResponse;
import com.kimtaeyang.mobidic.user.entity.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PronunciationService {
    private final WordService wordService;
    private final RestClient restClient;

    @Value("${whisper.flask-server-url}")
    private String flaskServerBaseUrl;

    public Double ratePronunciation(User user, UUID wordId, MultipartFile multipartFile) {
        Word word = wordService.getWordById(user, wordId);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", multipartFile.getResource());

        SttResponse sttResponse;
        try {
            sttResponse = restClient.post()
                    .uri(URI.create(flaskServerBaseUrl + "/transcribe"))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(builder.build())
                    .retrieve()
                    .body(SttResponse.class);
        } catch (RestClientException e) {
            throw new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (sttResponse == null || sttResponse.getResult() == null) {
            throw new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR);
        }

        return findSimilarity(word.getExpression(), sttResponse.getResult());
    }

    private int getLevenshteinDistance(String X, String Y) {
        int m = X.length();
        int n = Y.length();

        int[][] T = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            T[i][0] = i;
        }
        for (int j = 1; j <= n; j++) {
            T[0][j] = j;
        }

        int cost;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                cost = X.charAt(i - 1) == Y.charAt(j - 1) ? 0 : 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }

        return T[m][n];
    }

    public static int getDamerauLevenshteinDistance(
            @NonNull CharSequence source,
            @NonNull CharSequence target
    ) {
        int sourceLength = source.length();
        int targetLength = target.length();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1), dist[i - 1][j - 1] + cost);
                if (i > 1 &&
                        j > 1 &&
                        source.charAt(i - 1) == target.charAt(j - 2) &&
                        source.charAt(i - 2) == target.charAt(j - 1)) {
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
                }
            }
        }
        return dist[sourceLength][targetLength];
    }

    private double findSimilarity(String orgString, String compareString) {
        compareString = compareString.toLowerCase().trim();
        compareString = compareString.substring(0, compareString.length() - 1);

        double maxLength = Double.max(orgString.length(), compareString.length());
        if (maxLength > 0) {
            return (maxLength - getDamerauLevenshteinDistance(orgString, compareString)) / maxLength;
        }
        return 1.0;
    }

}
