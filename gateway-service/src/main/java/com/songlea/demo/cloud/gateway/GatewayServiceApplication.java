package com.songlea.demo.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 智能路由/服务网关中心
 *
 * @author Song Lea
 */
@SpringBootApplication
@EnableZuulProxy
@EnableCircuitBreaker
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

}
