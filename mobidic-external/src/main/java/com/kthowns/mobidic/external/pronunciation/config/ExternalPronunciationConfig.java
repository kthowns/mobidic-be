package com.kthowns.mobidic.external.pronunciation.config;

import com.kthowns.mobidic.external.pronunciation.properties.SttProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SttProperties.class)
public class ExternalPronunciationConfig {
}
