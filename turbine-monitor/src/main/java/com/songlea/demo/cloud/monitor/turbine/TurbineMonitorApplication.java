package com.songlea.demo.cloud.monitor.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

/**
 * 熔断监控Hystrix Dashboard和Turbine服务
 * turbine是聚合服务器发送事件流数据的一个工具，hystrix的监控中，只能监控单个节点，
 * 实际生产中都为集群，因此可以通过urbine来监控集群下hystrix的metrics情况，通过eureka来发现hystrix服务
 *
 * @author Song Lea
 */
@SpringBootApplication
@EnableHystrixDashboard
// @EnableTurbine注解包含了@EnableDiscoveryClient注解,
// 即开启了注册服务,故不需要再@EnableEurekaClient注册
@EnableTurbine
public class TurbineMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurbineMonitorApplication.class, args);
    }

    // 对404与500错误界面
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            container.addErrorPages(error404Page, error500Page);
        });
    }
}