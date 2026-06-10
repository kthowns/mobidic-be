package com.kthowns.mobidic.external.auth.client;

import com.kthowns.mobidic.common.code.GeneralResponseCode;
import com.kthowns.mobidic.common.exception.ApiException;
import com.kthowns.mobidic.domain.auth.client.OAuthClient;
import com.kthowns.mobidic.domain.auth.model.OAuthUserInfo;
import com.kthowns.mobidic.external.auth.dto.KakaoTokenResponse;
import com.kthowns.mobidic.external.auth.dto.KakaoUserInfo;
import com.kthowns.mobidic.external.auth.properties.KakaoApiProperties;
import com.kthowns.mobidic.external.auth.properties.KakaoApiUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient implements OAuthClient {
    private final RestClient restClient;
    private final KakaoApiProperties kakaoApiProperties;

    @Override
    public String getLoginUrl(boolean isDev, String platform, String serverBaseUrl) {
        return UriComponentsBuilder
                .fromUriString(KakaoApiUrl.CODE.getUrl())
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoApiProperties.restApiKey())
                .queryParam("redirect_uri", getBackendCallbackUrl(isDev, platform, serverBaseUrl))
                .encode()
                .toUriString();
    }

    @Override
    public String getAccessToken(String authCode, boolean isDev, String platform, String serverBaseUrl) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", kakaoApiProperties.restApiKey());
        formData.add("redirect_uri", getBackendCallbackUrl(isDev, platform, serverBaseUrl));
        formData.add("code", authCode);
        formData.add("client_secret", kakaoApiProperties.clientSecret());

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

        return Objects.requireNonNull(tokenResponse).getAccessToken();
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        KakaoUserInfo userInfo = restClient.get()
                .uri(KakaoApiUrl.ME.getUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    byte[] responseBody = response.getBody().readAllBytes();
                    String bodyContent = new String(responseBody, StandardCharsets.UTF_8);
                    log.error("카카오 User Info API 호출 실패: {} | 내용: {}", response.getStatusCode(), bodyContent);
                    throw new ApiException(GeneralResponseCode.EXTERNAL_SERVER_ERROR);
                })
                .body(KakaoUserInfo.class);

        return OAuthUserInfo.of(
                String.valueOf(userInfo.getId()),
                userInfo.getKakaoAccount().getEmail(),
                userInfo.getKakaoAccount().getProfile().getNickname()
        );
    }

    public String getBackendCallbackUrl(boolean isDev, String platform, String serverBaseUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                isDev ? kakaoApiProperties.dev().backendCallbackUrl()
                        : serverBaseUrl + "/api/auth/v1/oauth2/kakao"
        );

        if ("android".equals(platform)) {
            builder.queryParam("platform", "android");
        }

        if (isDev) {
            builder.queryParam("isDev", true);
        }

        return builder.toUriString();
    }
}
