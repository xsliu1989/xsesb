package com.xs.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * 实体来源有两处：
 * 1. com.xs.admin.entity —— 管理端自有实体（用户、角色、日志开关等）
 * 2. com.xs.datamodel.*  —— 复用 ESB 公共模块的元数据实体（渠道、交易接口等）
 * 默认实体扫描跟随 scanBasePackages，只覆盖 com.xs.admin，公共模块实体会被漏掉，
 * 导致 Criteria 查询报 "Not an entity"，这里显式声明全部实体包。
 */
@SpringBootApplication(scanBasePackages = {"com.xs.admin"})
@EntityScan({
        "com.xs.admin.entity",
        "com.xs.datamodel",
        "com.xs.datamodel.customer",
        "com.xs.datamodel.provider"
})
public class XrpcAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(XrpcAdminApplication.class, args);
    }
}
