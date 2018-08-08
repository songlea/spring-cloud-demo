package com.songlea.demo.cloud.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * hystrix-dashboard 监控服务
 *
 * @author Song Lea
 */
@SpringBootApplication
// 此注解已经包含@EnableEurekaClient功能
@EnableTurbine
// 注：当使用Hystrix Board来监控Spring Cloud Zuul构建的API网关时,Thread Pool信息会一直处于Loading状态;
// 这是由于Zuul默认会使用信号量来实现隔离,只有通过Hystrix配置把隔离机制改成为线程池的方式才能够得以展示.
@EnableHystrixDashboard
public class MonitorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorServiceApplication.class, args);
    }

}
