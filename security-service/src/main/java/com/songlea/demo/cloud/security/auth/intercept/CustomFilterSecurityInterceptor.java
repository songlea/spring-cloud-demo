package com.songlea.demo.cloud.security.auth.intercept;

import io.jsonwebtoken.lang.Assert;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * FilterSecurityInterceptor负责处理HTTP资源的安全性
 * 整个过程需要依赖AuthenticationManager、AccessDecisionManager和FilterInvocationSecurityMetadataSource.
 * AuthenticationManager：认证管理器，实现用户认证的入口
 * AccessDecisionManager：访问决策器，决定某个用户具有的角色，是否有足够的权限去访问某个资源
 * FilterInvocationSecurityMetadataSource：资源源数据定义，即定义某一资源可以被哪些角色访问
 *
 * @author Song Lea
 */
public class CustomFilterSecurityInterceptor extends FilterSecurityInterceptor {

    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    public CustomFilterSecurityInterceptor(FilterInvocationSecurityMetadataSource securityMetadataSource) {
        Assert.notNull(securityMetadataSource, "securityMetadataSource must be not null");
        this.securityMetadataSource = securityMetadataSource;
    }

    @Override
    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public void setSecurityMetadataSource(FilterInvocationSecurityMetadataSource newSource) {
        this.securityMetadataSource = newSource;
    }
}
