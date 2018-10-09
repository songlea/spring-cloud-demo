package com.songlea.demo.cloud.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * OAuth2认证服务
 *
 * @author Song Lea
 */
@SpringBootApplication
@EnableEurekaClient
@EnableTransactionManagement
@MapperScan(basePackages = {"com.songlea.demo.cloud.security.mapper"})
public class SecurityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityServiceApplication.class, args);
    }

}
