package com.songlea.demo.cloud.security.auth.intercept;

import com.songlea.demo.cloud.security.auth.userdetails.ExtendUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 从数据库中加载所有的URL与角色的映射关系以便权限验证
 * (用来储存请求与权限的对应关系,一般要自己重写)
 *
 * @author Song Lea
 * @see org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource
 * @since 2018-10-09 新建
 */
@Component
public class CustomInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final ExtendUserDetailsService extendUserDetailsService;

    /*
    访问资源即url时，会通过AbstractSecurityInterceptor拦截器拦截，
    其中会调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限，
    再调用授权管理器AccessDecisionManager，这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，
    还会获取被拦截的url和被拦截url所需的全部权限，然后根据所配的策略(一票决定，一票否定，少数服从多数等)，
    如果权限足够，则返回，权限不够则报错并调用权限不足页面。
    */
    @Autowired
    public CustomInvocationSecurityMetadataSource(ExtendUserDetailsService extendUserDetailsService) {
        Assert.notNull(extendUserDetailsService, "extendUserDetailsService must be not null");
        this.extendUserDetailsService = extendUserDetailsService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 后期可以考虑缓存URL与角色的关系
        Map<String, Collection<ConfigAttribute>> urlMappingRolesMap = extendUserDetailsService.loadUrlMappingRoles();
        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequest().getRequestURI();
        List<ConfigAttribute> result = new ArrayList<>();
        for (Map.Entry<String, Collection<ConfigAttribute>> entry : urlMappingRolesMap.entrySet()) {
            if (antPathMatcher.match(entry.getKey(), url)) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Map<String, Collection<ConfigAttribute>> urlMappingRolesMap = extendUserDetailsService.loadUrlMappingRoles();

        Set<ConfigAttribute> allAttributes = new HashSet<>();
        for (Map.Entry<String, Collection<ConfigAttribute>> entry : urlMappingRolesMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        // 一个是调用isAssignableFrom方法的类对象(后称对象a)，以及方法中作为参数的这个类对象(称之为对象b)，
        // 这两个对象如果满足以下条件则返回true，否则返回false：
        // a对象所对应类信息是b对象所对应的类信息的父类或者是父接口，简单理解即a是b的父类或接口
        // a对象所对应类信息与b对象所对应的类信息相同，简单理解即a和b为同一个类或同一个接口
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
