package com.songlea.demo.cloud.eureka.calculator.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
public class CalculatorService {

    private static final String LACK_PARAMETERS = "缺少参数！";

    public String add(BigDecimal a, BigDecimal b) {
        if (Objects.isNull(a) || Objects.isNull(b))
            return LACK_PARAMETERS;
        return a.add(b).toPlainString();
    }

    public String subtract(BigDecimal a, BigDecimal b) {
        if (Objects.isNull(a) || Objects.isNull(b))
            return LACK_PARAMETERS;
        return a.subtract(b).toPlainString();
    }

    public String multiply(BigDecimal a, BigDecimal b) {
        if (Objects.isNull(a) || Objects.isNull(b))
            return LACK_PARAMETERS;
        return a.multiply(b).toPlainString();
    }

    public String divide(BigDecimal a, BigDecimal b) {
        if (Objects.isNull(a) || Objects.isNull(b))
            return LACK_PARAMETERS;
        if (BigDecimal.ZERO.equals(b))
            return "除数不能为零！";
        return a.divide(b, 2, RoundingMode.HALF_UP).toPlainString();
    }
}
