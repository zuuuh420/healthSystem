package com.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 个人健康管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.health.mapper")
public class HealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }
}
