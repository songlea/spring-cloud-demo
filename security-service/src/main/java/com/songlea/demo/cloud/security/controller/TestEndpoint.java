package com.songlea.demo.cloud.security.controller;

import com.songlea.demo.cloud.security.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建一个endpoint作为提供给外部的接口：
 * 暴露一个商品查询接口，后续不做安全限制；一个订单查询接口，后续添加访问控制
 */
@RestController
public class TestEndpoint {

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable String id) {

        //for debug
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(sysUserMapper.selectByPrimaryKey(1));
        return "product id : " + id;
    }

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable String id) {
        //for debug
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "order id : " + id;
    }

}
