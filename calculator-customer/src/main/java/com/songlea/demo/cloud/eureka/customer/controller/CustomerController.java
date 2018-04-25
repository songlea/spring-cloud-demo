package com.songlea.demo.cloud.eureka.customer.controller;

import com.songlea.demo.cloud.eureka.customer.service.CustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 服务调用方(feign)
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @RequestMapping("/add")
    public String add(BigDecimal a, BigDecimal b) {
        return customerService.add(a, b);
    }

    @RequestMapping("/subtract")
    public String subtract(BigDecimal a, BigDecimal b) {
        return customerService.subtract(a, b);
    }

    @RequestMapping("/multiply")
    public String multiply(BigDecimal a, BigDecimal b) {
        return customerService.multiply(a, b);
    }

    @RequestMapping("/divide")
    public String divide(BigDecimal a, BigDecimal b) {
        return customerService.divide(a, b);
    }
}
