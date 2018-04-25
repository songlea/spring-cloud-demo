package com.songlea.demo.cloud.eureka.customer.service;

import com.songlea.demo.cloud.eureka.customer.config.CustomerFeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 绑定該接口到CalculatorServer服务并通知Feign组件对该接口进行代理（不需要编写接口实现）
 * Feign是一种声明式、模板化的HTTP客户端。
 * 在Spring Cloud中使用Feign, 我们可以做到使用HTTP请求远程服务时能与调用本地方法一样的编码体验，
 * 开发者完全感知不到这是远程方法，更感知不到这是个HTTP请求。
 */
// 用于通知Feign组件对该接口进行代理(不需要编写接口实现)，使用者可直接通过@Autowired注入
// value指定需要调用的serviceId名,同name与serviceId
// decode404 = false表示是否对404进行解码,false时会进入到fallback
@FeignClient(value = "calculator-service", configuration = CustomerFeignConfig.class,
        fallback = CustomerService.DefaultCalculatorService.class)
@Primary
public interface CustomerService {

    String DEFAULT_ERROR = "系统繁忙，请稍后再试！";

    // @RequestMapping表示在调用该方法时需要向/group/{groupId}发送GET请求。
    // @PathVariable与SpringMVC中对应注解含义相同
    @RequestMapping(value = "/calculator/add", method = RequestMethod.GET)
    String add(@RequestParam(value = "a", required = false) BigDecimal a,
               @RequestParam(value = "b", required = false) BigDecimal b);

    @RequestMapping(value = "/calculator/subtract", method = RequestMethod.GET)
    String subtract(@RequestParam(value = "a", required = false) BigDecimal a,
                    @RequestParam(value = "b", required = false) BigDecimal b);

    @RequestMapping(value = "/calculator/multiply", method = RequestMethod.GET)
    String multiply(@RequestParam(value = "a", required = false) BigDecimal a,
                    @RequestParam(value = "b", required = false) BigDecimal b);

    @RequestMapping(value = "/calculator/divide", method = RequestMethod.GET)
    String divide(@RequestParam(value = "a", required = false) BigDecimal a,
                  @RequestParam(value = "b", required = false) BigDecimal b);

    /**
     * 这里采用和SpringCloud官方文档相同的做法，把fallback类作为内部类放入Feign接口中
     * http://cloud.spring.io/spring-cloud-static/Camden.SR6/#spring-cloud-feign-hystrix
     * （也可以外面独立定义该类，个人觉得没必要，这种东西写成内部类最合适）
     */
    @Component
    class DefaultCalculatorService implements CustomerService {

        @Override
        public String add(BigDecimal a, BigDecimal b) {
            return DEFAULT_ERROR;
        }

        @Override
        public String subtract(BigDecimal a, BigDecimal b) {
            return DEFAULT_ERROR;
        }

        @Override
        public String multiply(BigDecimal a, BigDecimal b) {
            return DEFAULT_ERROR;
        }

        @Override
        public String divide(BigDecimal a, BigDecimal b) {
            return DEFAULT_ERROR;
        }
    }
}