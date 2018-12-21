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

    /*
    @Secured({ "ROLE_DBA", "ROLE_ADMIN" }):(角色之间为或关系)
        用来定义业务方法的安全性配置属性列表，可以使用@Secured在方法上指定安全性要求[角色/权限等]，只有对应角色/权限的用户才可以调用这些方法。
        如果有人试图调用一个方法，但是不拥有所需的角色/权限，那会将会拒绝访问将引发异常；
        它有一个缺点(限制)就是不支持Spring EL表达式。
    @PreAuthorize("hasRole('ADMIN') AND hasRole('DBA')") / @PostAuthorize("returnObject.type == authentication.name"):
        首选应用到方法级安全性的方式，并支持Spring表达式语言，也提供基于表达式的访问控制。
        @PreAuthorize适合进入方法之前验证授权；
        @PostAuthorize检查授权方法之后才被执行，所以它适合用在对返回的值作验证授权。
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"}):(角色之间为或关系)
        JSR-250注解，表示访问对应方法时所应该具有的角色。
    @PermitAll/@DenyAll
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResponseEntity<String> insert(@RequestBody SysUser sysUser) {
        String password = sysUser.getPassword();
        sysUser.setPassword(passwordEncoder.encode(password));
        sysUserMapper.insertSelective(sysUser);
        return new ResponseEntity<>("保存成功", HttpStatus.OK);
    }

}
