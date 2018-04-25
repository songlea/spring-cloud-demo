package com.songlea.demo.cloud.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 智能路由/服务网关：
 * 服务提供方和消费方都注册到注册中心，使得消费方能够直接通过 ServiceId 访问服务方
 * 实际情况是：通常我们的服务方可能都需要做 接口权限校验、限流、软负载均衡 等等
 * 而这类工作，完全可以交给服务方的更上一层：服务网关，来集中处理
 * 这样的目的：保证微服务的无状态性，使其更专注于业务处理
 * 所以说，服务网关是微服务架构中一个很重要的节点，Spring Cloud Netflix 中的 Zuul 就担任了这样的角色
 */
@SpringBootApplication
@EnableZuulProxy
public class GatewayApplication {

    /*
    通过服务路由的功能，可以在对外提供服务时，只暴露 Zuul 中配置的调用地址，而调用方就不需要了解后端具体的微服务主机
    Zuul 提供了两种映射方式：URL 映射和 ServiceId 映射（后者需要将 Zuul 注册到注册中心，使之能够发现后端的微服务）
    ServiceId 映射的好处是：它支持软负载均衡，基于 URL 的方式是不支持的（实际测试也的确如此）
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
