package com.kthowns.mobidic.external.pronunciation;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.pronunciation.client.SpeechToTextClient;
import com.kthowns.mobidic.domain.pronunciation.model.SttResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class WhisperSttClient implements SpeechToTextClient {
    private final RestClient restClient;

    @Value("${whisper.flask-server-url}")
    private String flaskServerBaseUrl;

    @Override
    public String transcribe(MultipartFile file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource());

        try {
            SttResponse sttResponse = restClient.post()
                    .uri(URI.create(flaskServerBaseUrl + "/transcribe"))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(builder.build())
                    .retrieve()
                    .body(SttResponse.class);

            if (sttResponse == null || sttResponse.result() == null) {
                throw new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR);
            }

            return sttResponse.result();
        } catch (RestClientException e) {
            throw new ApiException(GeneralResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
