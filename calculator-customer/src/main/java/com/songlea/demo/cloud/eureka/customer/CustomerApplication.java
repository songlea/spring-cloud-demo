package com.songlea.demo.cloud.eureka.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * 服务消费端
 * Spring Cloud Netflix 的微服务都是以 HTTP 接口的形式暴露的，所以可以用 Apache 的 HttpClient 或 Spring 的 RestTemplate 去調用
 * 而 Feign 是一個使用起來更加方便的 HTTP 客戶端，它用起來就好像調用本地方法一樣，完全感覺不到是調用的遠程方法
 * 总结起来就是：发布到注册中心的服务方接口，是 HTTP 的，也可以不用 Ribbon 或者 Feign，直接浏览器一样能够访问
 * 只不过 Ribbon 或者 Feign 调用起来要方便一些，最重要的是：它俩都支持软负载均衡
 *
 * @author Song Lea
 */
@SpringBootApplication
@EnableEurekaClient
/* 通过EnableFeignClients调用其他服务的api */
@EnableFeignClients
/*
1.断路器机制
     断路器很好理解, 当Hystrix Command请求后端服务失败数量超过一定比例(默认50%), 断路器会切换到开路状态(Open).
     这时所有请求会直接失败而不会发送到后端服务. 断路器保持在开路状态一段时间后(默认5秒), 自动切换到半开路状态(HALF-OPEN).
     这时会判断下一次请求的返回情况, 如果请求成功, 断路器切回闭路状态(CLOSED), 否则重新切换到开路状态(OPEN).
     Hystrix的断路器就像我们家庭电路中的保险丝, 一旦后端服务不可用, 断路器会直接切断请求链, 避免发送大量无效请求影响系统吞吐量,
     并且断路器有自我检测并恢复的能力
2.Fallback
     Fallback相当于是降级操作. 对于查询操作, 我们可以实现一个fallback方法, 当请求后端服务出现异常的时候, 可以使用fallback方法返回的值.
     fallback方法的返回值一般是设置的默认值或者来自缓存.
3.资源隔离
     在Hystrix中, 主要通过线程池来实现资源隔离. 通常在使用的时候我们会根据调用的远程服务划分出多个线程池.
     但是带来的代价就是维护多个线程池会对系统带来额外的性能开销. 如果是对性能有严格要求而且确信自己调用服务的客户端代码不会出问题的话,
     可以使用Hystrix的信号模式(Semaphores)来隔离资源
 */
/*
    1、构建Hystrix的Command对象, 调用执行方法.
    2、Hystrix检查当前服务的熔断器开关是否开启, 若开启, 则执行降级服务getFallback方法.
    3、若熔断器开关关闭, 则Hystrix检查当前服务的线程池是否能接收新的请求, 若超过线程池已满, 则执行降级服务getFallback方法.
    4、若线程池接受请求, 则Hystrix开始执行服务调用具体逻辑run方法.
    5、若服务执行失败, 则执行降级服务getFallback方法, 并将执行结果上报Metrics更新服务健康状况.
    6、若服务执行超时, 则执行降级服务getFallback方法, 并将执行结果上报Metrics更新服务健康状况.
    7、若服务执行成功, 返回正常结果.
    8、若服务降级方法getFallback执行成功, 则返回降级结果.
    9、若服务降级方法getFallback执行失败, 则抛出异常..0
 */
@EnableCircuitBreaker
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

}
