package com.songlea.demo.cloud.business.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 业务层服务测试Controller
 *
 * @author Song Lea
 */
@RestController
@RequestMapping("/business")
// 只有加了此注解才能被/actuator/bus-refresh更新
@RefreshScope
public class HelloWorldController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldController.class);

    // 配置项放在在git服务器上(business-service-dev.yml)
    @Value("${business-service.demo.msg}")
    private String msg;

    @RequestMapping(value = "/test")
    public String hellWorld(HttpServletRequest request) {
        LOGGER.info("业务服务层收到请求：{}", request.getRequestURI());
        return msg;
    }
}