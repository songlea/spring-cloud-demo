package com.songlea.demo.cloud.security.filter;

import com.songlea.demo.cloud.security.model.db.SysMenu;
import com.songlea.demo.cloud.security.model.db.SysRole;
import com.songlea.demo.cloud.security.service.PermissionService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 从数据库中加载所有的ConfigAttribute,以便权限验证
 *
 * @author Song Lea
 * @see org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource
 * @since 2018-10-09 新建
 */
public class CustomInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private Map<String, Collection<ConfigAttribute>> urlMappingRolesMap;
    private FilterInvocationSecurityMetadataSource superMetadataSource;
    private PermissionService permissionService;

    public CustomInvocationSecurityMetadataSource(FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource,
                                                  PermissionService permissionService) {
        Assert.notNull(permissionService, "permissionService must be not null");
        this.superMetadataSource = filterInvocationSecurityMetadataSource;
        this.permissionService = permissionService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        reloadUrlMappingRolesMap();

        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequest().getRequestURI();
        List<ConfigAttribute> result = new ArrayList<>();
        for (Map.Entry<String, Collection<ConfigAttribute>> entry : urlMappingRolesMap.entrySet()) {
            if (antPathMatcher.match(entry.getKey(), url)) {
                result.addAll(entry.getValue());
            }
        }
        if (superMetadataSource != null) {
            result.addAll(superMetadataSource.getAttributes(object));
        }
        return result;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        reloadUrlMappingRolesMap();

        Set<ConfigAttribute> allAttributes = new HashSet<>();
        for (Map.Entry<String, Collection<ConfigAttribute>> entry : urlMappingRolesMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }
        if (superMetadataSource != null) {
            allAttributes.addAll(superMetadataSource.getAllConfigAttributes());
        }
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        // 一个是调用isAssignableFrom方法的类对象(后称对象a)，以及方法中作为参数的这个类对象(称之为对象b)，这两个对象如果满足以下条件则返回true，否则返回false：
        // a对象所对应类信息是b对象所对应的类信息的父类或者是父接口，简单理解即a是b的父类或接口
        // a对象所对应类信息与b对象所对应的类信息相同，简单理解即a和b为同一个类或同一个接口
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    // 每次请求重新加载对应的URL与roles映射关系
    // 后期可以放到缓存中
    private void reloadUrlMappingRolesMap() {
        urlMappingRolesMap = new LinkedHashMap<>();
        List<SysMenu> sysMenus = permissionService.selectAllSysMenu();
        if (!CollectionUtils.isEmpty(sysMenus)) {
            for (SysMenu sysMenu : sysMenus) {
                List<SysRole> sysRoles = permissionService.selectSysRoleByMenuId(sysMenu.getId());
                if (!CollectionUtils.isEmpty(sysRoles)) {
                    List<ConfigAttribute> array = new ArrayList<>();
                    for (SysRole sysRole : sysRoles) {
                        ConfigAttribute cfg = new SecurityConfig(sysRole.getCode());
                        array.add(cfg);
                    }
                    urlMappingRolesMap.put(sysMenu.getUrl(), array);
                }
            }
        }
    }
}
