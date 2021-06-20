package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tanhua.face")
public class FaceProperties {
    private String appId;
    private String apiKey;
    private String secretKey;
}
