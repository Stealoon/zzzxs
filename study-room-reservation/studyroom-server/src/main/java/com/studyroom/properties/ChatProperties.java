package com.studyroom.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 智能客服配置属性
 */
@Component
@ConfigurationProperties(prefix = "studyroom.chat")
@Data
public class ChatProperties {

    private boolean enabled;
    private String baseUrl;
    private String apiKey;
    private String model;
    private int contextMaxRounds = 5;
    private int contextTtlMinutes = 30;
}
