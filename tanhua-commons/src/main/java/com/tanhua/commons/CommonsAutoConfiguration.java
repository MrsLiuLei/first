package com.tanhua.commons;

import com.tanhua.commons.properties.*;
import com.tanhua.commons.templates.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({SmsProperties.class, OssProperties.class, FaceProperties.class, HuanXinProperties.class,HuaWeiUGCProperties.class})
public class CommonsAutoConfiguration {
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        SmsTemplate smsTemplate = new SmsTemplate(smsProperties);
        smsTemplate.init();
        return smsTemplate;
    }
    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        OssTemplate ossTemplate = new OssTemplate(ossProperties);
        return ossTemplate;
    }
    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties huanXinProperties){
        HuanXinTemplate huanXinTemplate = new HuanXinTemplate(huanXinProperties);
        return huanXinTemplate;
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }
    @Bean
    public FaceTemplate faceTemplate(FaceProperties faceProperties){
        return new FaceTemplate(faceProperties);
    }
    @Bean
    public HuaWeiUGCTemplate huaWeiUGCTemplate(HuaWeiUGCProperties properties) {
        return new HuaWeiUGCTemplate(properties);
    }
}
