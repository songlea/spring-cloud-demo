package com.songlea.demo.cloud.eureka.customer.config;

import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * feign客户端配置,默认的配置类为FeignClientsConfiguration,Feign的源码实现的过程如下：
 * 1、首先通过@EnableFeignCleints注解开启FeignCleint
 * 2、根据Feign的规则实现接口，并加@FeignCleint注解
 * 3、程序启动后，会进行包扫描，扫描所有的@FeignCleint的注解的类，并将这些信息注入到ioc容器中
 * 4、当接口的方法被调用，通过jdk的代理，来生成具体的RequesTemplate
 * 5、RequesTemplate在生成Request
 * 6、Request交给Client去处理，其中Client可以是HttpUrlConnection、HttpClient也可以是Okhttp
 * 7、最后Client被封装到LoadBalanceClient类，这个类结合类Ribbon做到了负载均衡
 *
 * @author Song Lea
 */
@Configuration
public class CustomerFeignConfig {

    @Value("${feign.retryer.enable}")
    private boolean retry;

    @Value("${feign.retryer.maxAttempts}")
    private int maxAttempts;

    @Value("${feign.retryer.period}")
    private long period;

    @Value("${feign.retryer.maxPeriod}")
    private long maxPeriod;

    // 调用失败时的重试机制
    @Bean
    public Retryer feignRetry() {
        if (!retry)
            return Retryer.NEVER_RETRY;
        else
            return new Retryer.Default(period, TimeUnit.SECONDS.toMillis(maxPeriod), maxAttempts);
    }
}
