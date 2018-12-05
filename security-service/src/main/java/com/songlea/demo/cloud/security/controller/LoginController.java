package com.songlea.demo.cloud.security.controller;

import com.songlea.demo.cloud.security.model.dto.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录与登出管理
 *
 * @author Song Lea
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    // 登录
    @RequestMapping(value = "/in", method = RequestMethod.POST)
    public ResultData login(String username, String password) {
        LOGGER.info("登录 username:{},password:{}", username, password);
        return null;
    }

}
