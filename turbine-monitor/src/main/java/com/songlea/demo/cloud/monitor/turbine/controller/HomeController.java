package com.songlea.demo.cloud.monitor.turbine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求重定向到hystrix监控的主界面
 *
 * @author Song Lea
 */
@Controller
public class HomeController {

    private static final String HYSTRIX_URL_PATH = "/hystrix";

    @RequestMapping("/")
    public String home(HttpServletRequest request) {
        return "redirect:" + request.getContextPath() + HYSTRIX_URL_PATH;
    }
}
