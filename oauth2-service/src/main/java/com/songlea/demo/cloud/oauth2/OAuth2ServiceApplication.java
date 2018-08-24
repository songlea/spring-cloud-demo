package com.songlea.demo.cloud.oauth2;

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
// @EnableTransactionManagement确保在创建令牌时防止竞争相同行的客户端应用程序之间发生冲突
@EnableTransactionManagement
public class OAuth2ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ServiceApplication.class, args);
    }

}
