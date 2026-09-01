package com.health;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 个人健康管理系统启动类
 *
 * @author health-team
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.health.mapper")
public class HealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
        log.info("Health management system started successfully. API base path: /api");
    }
}
