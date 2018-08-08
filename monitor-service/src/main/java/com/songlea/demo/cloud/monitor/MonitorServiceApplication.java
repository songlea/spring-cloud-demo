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
@EnableHystrixDashboard
public class MonitorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorServiceApplication.class, args);
    }

}
