package com.kimtaeyang.mobidic.auth.service;

import com.kimtaeyang.mobidic.auth.dto.KakaoLoginUrlResponse;
import com.kimtaeyang.mobidic.auth.dto.KakaoTokenResponse;
import com.kimtaeyang.mobidic.auth.dto.KakaoUserInfo;
import com.kimtaeyang.mobidic.auth.util.KakaoProperties;
import com.kimtaeyang.mobidic.common.code.GeneralResponseCode;
import com.kimtaeyang.mobidic.common.code.KakaoApiUrl;
import com.kimtaeyang.mobidic.common.exception.ApiException;
import com.kimtaeyang.mobidic.user.entity.User;
import com.kimtaeyang.mobidic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {
    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> getUserByKakaoId(Long kakaoId) {
        return userRepository.findByKakaoId(kakaoId);
    }

    public KakaoLoginUrlResponse getKakaoLoginUrl(boolean isDev, String platform) {
        String resultUrl = UriComponentsBuilder
                .fromUriString(KakaoApiUrl.CODE.getUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoProperties.getClientId())
                .queryParam("redirect_uri", kakaoProperties.getBackendCallbackUrl(isDev, platform))
                .encode()
                .toUriString();

        return new KakaoLoginUrlResponse(resultUrl);
    }

    public KakaoUserInfo getKakaoUserInfo(String kakaoAccessToken) {
        KakaoUserInfo userInfo = restClient.get()
                .uri(KakaoApiUrl.ME.getUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    byte[] responseBody = response.getBody().readAllBytes();
                    String bodyContent = new String(responseBody, StandardCharsets.UTF_8);

                    log.error("카카오 User Info API 호출 실패: {} | 내용: {}", response.getStatusCode(), bodyContent);
                    throw new ApiException(GeneralResponseCode.EXTERNAL_SERVER_ERROR);
                })
                .body(KakaoUserInfo.class);

        log.info("userinfo : {}", userInfo);

        return userInfo;
    }

    public String getKakaoAccessToken(String code, boolean isDev, String platform) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoProperties.getClientId());
        formData.add("redirect_uri", kakaoProperties.getBackendCallbackUrl(isDev, platform));
        formData.add("code", code);
        formData.add("client_secret", kakaoProperties.getClientSecret());

        KakaoTokenResponse tokenResponse = restClient.post()
                .uri(KakaoApiUrl.TOKEN.getUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(formData)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    byte[] responseBody = response.getBody().readAllBytes();
                    String bodyContent = new String(responseBody, StandardCharsets.UTF_8);

                    log.error("카카오 Access Token API 호출 실패: {} {}", response.getStatusCode(), bodyContent);
                    throw new ApiException(GeneralResponseCode.EXTERNAL_SERVER_ERROR);
                })
                .body(KakaoTokenResponse.class);

        log.info("kakao access token : {}", tokenResponse.getAccessToken());

        return Objects.requireNonNull(tokenResponse).getAccessToken();
    }
}
