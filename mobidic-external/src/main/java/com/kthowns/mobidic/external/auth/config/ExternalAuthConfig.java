package com.kthowns.mobidic.external.auth.config;

import com.kthowns.mobidic.external.auth.properties.KakaoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KakaoProperties.class})
public class ExternalAuthConfig {
}
