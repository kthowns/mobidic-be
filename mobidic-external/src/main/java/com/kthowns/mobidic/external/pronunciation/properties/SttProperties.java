package com.kthowns.mobidic.external.pronunciation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("whisper")
public record SttProperties(String flaskServerUrl) {
}
