package com.songlea.demo.cloud.eureka.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

/**
 * 创建服务注册中心必须包括：
 * 1、管理服务实例
 * 2、提供服务注册或下线
 * 3、提供服务发现
 * 4、提供服务注册表至两类客户端(即服务提供者与消费者)
 *
 * @author Song Lea
 */
@EnableEurekaServer
@SpringBootApplication
public class DiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        // 404与500错误的跳转界面
        return (servletContainer -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            servletContainer.addErrorPages(error404Page, error500Page);
        });
    }
}