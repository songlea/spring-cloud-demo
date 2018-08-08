package com.songlea.demo.cloud.gateway;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * 智能路由/服务网关中心
 *
 * @author Song Lea
 */
@SpringBootApplication
@EnableZuulProxy
@EnableCircuitBreaker
public class GatewayServiceApplication {

    private static final String HYSTRIX_STREAM_PATH = "/hystrix.stream";
    private static final String HYSTRIX_SERVLET_NAME = "HystrixMetricsStreamServlet";

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    // 注册,否则dashboard界面无法访问到
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(999);
        registrationBean.addUrlMappings(HYSTRIX_STREAM_PATH);
        registrationBean.setName(HYSTRIX_SERVLET_NAME);
        return registrationBean;
    }
}
