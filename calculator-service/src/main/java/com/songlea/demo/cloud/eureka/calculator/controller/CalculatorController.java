package com.songlea.demo.cloud.eureka.calculator.controller;

import com.songlea.demo.cloud.eureka.calculator.service.CalculatorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 服务提供方暴露的数学运算服务
 */
@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    @Resource
    private CalculatorService calculatorService;

    @RequestMapping("/add")
    public String add(BigDecimal a, BigDecimal b) {
        return calculatorService.add(a, b);
    }

    @RequestMapping("/subtract")
    public String subtract(BigDecimal a, BigDecimal b) {
        return calculatorService.subtract(a, b);
    }

    @RequestMapping("/multiply")
    public String multiply(BigDecimal a, BigDecimal b) {
        return calculatorService.multiply(a, b);
    }

    @RequestMapping("/divide")
    public String divide(BigDecimal a, BigDecimal b) {
        return calculatorService.divide(a, b);
    }
}