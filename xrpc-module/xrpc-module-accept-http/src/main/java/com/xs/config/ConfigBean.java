package com.xs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author lxs
 * @Configuration 相当于 spring中的 application.xml
 */
@Configuration
public class ConfigBean {

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
