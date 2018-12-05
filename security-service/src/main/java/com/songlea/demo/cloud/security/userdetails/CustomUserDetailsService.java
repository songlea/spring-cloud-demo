package com.songlea.demo.cloud.security.userdetails;

import com.songlea.demo.cloud.security.model.db.SysRole;
import com.songlea.demo.cloud.security.model.db.SysUser;
import com.songlea.demo.cloud.security.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义的UserDetailsService(通过用户名查询用户并返回其GrantedAuthority(权限)列表)：
 * AuthenticationManager(身份认证管理器)可以通过applicationContext-security.xml中<authentication-manager />标签实现，
 * 该标签需要引用一个实现了UserDetailService接口的类，该类的loadUserByUsername(String username)方法，
 * 通过传进来的用户名返回一个User对象，构造该User对象时需要传入GrantedAuthority的Collection，此时可以通过不同的用户名赋予不同的GrantedAuthority。
 *
 * @author Song Lea
 */
@Slf4j
public class CustomUserDetailsService implements UserDetailsService, MessageSourceAware {

    // 默认权限集合为空
    private static final List<GrantedAuthority> NO_AUTHORITIES = Collections.emptyList();
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    // 权限名前缀
    private String rolePrefix = "";
    private boolean usernameBasedPrimaryKey = true;
    private boolean enableAuthorities = true;

    // 用户在数据库中通过用户名来查询用户
    private PermissionService permissionService;

    public CustomUserDetailsService(PermissionService permissionService) {
        Assert.notNull(permissionService, "permissionService cannot be null");
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails detailsWrapper = loadUsersByUsername(username);
        if (detailsWrapper == null) {
            LOGGER.info("Query returned no results for user account  '" + username + "'");
            throw new UsernameNotFoundException(
                    this.messages.getMessage("PermissionService.selectByPrimaryKey.notFound",
                            new Object[]{username}, "Username {0} not found"));
        }

        Set<GrantedAuthority> dbAuthSet = new HashSet<>();
        if (this.enableAuthorities) {
            List<GrantedAuthority> grantedAuthorities = loadUserAuthorities(detailsWrapper.getCurrentUserId());
            if (grantedAuthorities != null && !grantedAuthorities.isEmpty())
                dbAuthSet.addAll(grantedAuthorities);
        }
        List<GrantedAuthority> dbAuthList = new ArrayList<>(dbAuthSet);

        if (dbAuthList.size() == 0) {
            LOGGER.info("User '" + username + "' has no authorities and will be treated as 'not found'");
            throw new UsernameNotFoundException(this.messages.getMessage(
                    "PermissionService.listSysRoleByUserId.noAuthority",
                    new Object[]{username}, "User {0} has no GrantedAuthority"));
        }
        return createUserDetails(username, detailsWrapper, dbAuthList);
    }

    @Override
    public void setMessageSource(@NotNull MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }

    // 设置role前缀
    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    // 是否使用用户表中的username构建UserDetails对象,默认true
    public void setUsernameBasedPrimaryKey(boolean usernameBasedPrimaryKey) {
        this.usernameBasedPrimaryKey = usernameBasedPrimaryKey;
    }

    // 是否加载权限集合,默认为true
    public void setEnableAuthorities(boolean enableAuthorities) {
        this.enableAuthorities = enableAuthorities;
    }

    // 获取UserDetails,未查询到则返回null
    private CustomUserDetails loadUsersByUsername(String username) {
        Optional<SysUser> optionalSysUser = permissionService.selectSysUserByAccount(username);
        if (optionalSysUser.isPresent()) {
            SysUser sysUser = optionalSysUser.get();
            return CustomUserDetails.withUsername(username).id(sysUser.getId())
                    .password(sysUser.getPassword()).status(sysUser.getStatus())
                    .authorities(NO_AUTHORITIES).build();
        }
        return null;
    }

    // 获取对应username的权限集合,未查询到则返回null
    private List<GrantedAuthority> loadUserAuthorities(Integer userId) {
        List<SysRole> sysRoles = permissionService.listSysRoleByUserId(userId);
        if (sysRoles != null && !sysRoles.isEmpty()) {
            return sysRoles.stream().map(t -> new SimpleGrantedAuthority(this.rolePrefix + t.getCode()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    // 构建返回的UserDetails对象
    private UserDetails createUserDetails(String username, CustomUserDetails userFromUserQuery,
                                          List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();
        if (!this.usernameBasedPrimaryKey) {
            returnUsername = username;
        }
        return new CustomUserDetails(userFromUserQuery.getCurrentUserId(), returnUsername,
                userFromUserQuery.getPassword(), combinedAuthorities);
    }
}
