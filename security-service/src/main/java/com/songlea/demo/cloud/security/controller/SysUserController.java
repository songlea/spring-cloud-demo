package com.songlea.demo.cloud.security.controller;

import com.songlea.demo.cloud.security.mapper.SysUserMapper;
import com.songlea.demo.cloud.security.model.db.SysUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户表管理
 *
 * @author Song Lea
 */
@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysUserMapper sysUserMapper;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResponseEntity<String> insert(@RequestBody SysUser sysUser) {
        String password = sysUser.getPassword();
        sysUser.setPassword(passwordEncoder.encode(password));
        sysUserMapper.insertSelective(sysUser);
        return new ResponseEntity<>("保存成功", HttpStatus.OK);
    }

}
