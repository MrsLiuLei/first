package com.tanhua.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "tanhua.huawei")
public class HuaWeiUGCProperties {
    private String username;
    private String password;
    private String project;
    private String domain;
    private String categoriesText;
    private String categoriesImage;
    private String textApiUrl;
    private String imageApiUrl;
}
