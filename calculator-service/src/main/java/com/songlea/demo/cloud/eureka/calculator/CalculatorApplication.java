package com.songlea.demo.cloud.eureka.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 计算服务模块
 *
 * @author Song Lea
 */
@SpringBootApplication
/*
Eureka client 负责与Eureka Server 配合向外提供注册与发现服务接口。
    @EnableDiscoveryClient注解是基于spring-cloud-commons依赖，并且在classpath中实现；
    @EnableEurekaClient注解是基于spring-cloud-netflix依赖，只能为eureka作用；
通过@EnableEurekaClient这个简单的注解，在spring cloud应用启动的时候，
就可以把EurekaDiscoveryClient注入，继而使用NetFlix提供的Eureka client。
 */
@EnableEurekaClient
public class CalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }

}
