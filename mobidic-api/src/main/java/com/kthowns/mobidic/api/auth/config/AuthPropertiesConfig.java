package com.kthowns.mobidic.api.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OAuthKakaoProperties.class)
public class AuthPropertiesConfig {
}
