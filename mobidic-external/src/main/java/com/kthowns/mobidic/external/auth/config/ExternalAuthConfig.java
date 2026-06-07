package com.kthowns.mobidic.external.auth.config;

import com.kthowns.mobidic.external.auth.properties.KakaoApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KakaoApiProperties.class})
public class ExternalAuthConfig {
}
