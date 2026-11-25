package com.xs.admin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(OpenApiConfig.class);

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("ESB 管理控制台 API")
                .version("1.0")
                .description("配置 Tag 源/目标数据、渠道与数据源接口、发布与审计"));
    }
}
