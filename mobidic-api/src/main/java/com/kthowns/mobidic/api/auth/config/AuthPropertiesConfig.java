package com.kthowns.mobidic.api.auth.config;

import com.kthowns.mobidic.api.auth.properties.OAuthKakaoCallbackUrlProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OAuthKakaoCallbackUrlProperties.class)
public class AuthPropertiesConfig {
}
