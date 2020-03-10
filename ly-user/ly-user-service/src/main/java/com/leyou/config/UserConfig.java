package com.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix="ly.sms")
@Data
@Configuration
public class UserConfig {
    private String exchange;
    private String routingKey;
    private Long timeout;
}
