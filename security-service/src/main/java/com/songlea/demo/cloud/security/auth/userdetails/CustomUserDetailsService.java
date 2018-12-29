package com.songlea.demo.cloud.security.auth.userdetails;

import com.songlea.demo.cloud.security.model.db.SysMenu;
import com.songlea.demo.cloud.security.model.db.SysRole;
import com.songlea.demo.cloud.security.model.db.SysUser;
import com.songlea.demo.cloud.security.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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
@Component
public class CustomUserDetailsService implements ExtendUserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private static final List<GrantedAuthority> NO_AUTHORITIES = Collections.emptyList();

    private String rolePrefix = "";
    private boolean usernameBasedPrimaryKey = true;
    private boolean enableAuthorities = true;

    private final PermissionService permissionService;
    private final UserCache userCache;

    @Autowired
    public CustomUserDetailsService(PermissionService permissionService, UserCache userCache) {
        Assert.notNull(permissionService, "permissionService cannot be null");
        Assert.notNull(userCache, "userCache cannot be null");
        this.permissionService = permissionService;
        this.userCache = userCache;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 缓存中获取
        UserDetails user = userCache.getUserFromCache(username);
        if (user != null) {
            LOGGER.debug("Query returned from cache, username: {}", username);
            return user;
        }
        // 若缓存中没有再从DB中获取
        CustomUserDetails detailsWrapper = loadUsersByUsername(username);
        if (detailsWrapper == null) {
            LOGGER.error("Query returned no results for user account  '" + username + "'");
            throw new UsernameNotFoundException("User not found: " + username);
        }

        Set<GrantedAuthority> dbAuthSet = new HashSet<>();
        if (this.enableAuthorities) {
            List<GrantedAuthority> grantedAuthorities = loadUserAuthorities(detailsWrapper.getCurrentUserId());
            if (grantedAuthorities != null && !grantedAuthorities.isEmpty())
                dbAuthSet.addAll(grantedAuthorities);
        }
        List<GrantedAuthority> dbAuthList = new ArrayList<>(dbAuthSet);

        if (dbAuthList.size() == 0) {
            LOGGER.error("User '" + username + "' has no authorities and will be treated as 'not found'");
            throw new InsufficientAuthenticationException("User has no roles assigned");
        }
        UserDetails userDetails = createUserDetails(username, detailsWrapper, dbAuthList);
        // 保存于缓存中
        userCache.putUserInCache(userDetails);
        return userDetails;
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
        return CustomUserDetails.withUsername(returnUsername)
                .id(userFromUserQuery.getCurrentUserId())
                .password(userFromUserQuery.getPassword())
                .status(userFromUserQuery.getStatus())
                .authorities(combinedAuthorities).build();
    }

    @Override
    public List<SysMenu> selectAllSysMenu() {
        return permissionService.selectAllSysMenu();
    }

    @Override
    public List<SysRole> selectSysRoleByMenuId(Integer menuId) {
        return permissionService.selectSysRoleByMenuId(menuId);
    }
}
