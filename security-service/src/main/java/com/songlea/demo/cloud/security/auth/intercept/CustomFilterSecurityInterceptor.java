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
    // private AccessDecisionManager accessDecisionManager;

    public CustomFilterSecurityInterceptor(FilterInvocationSecurityMetadataSource securityMetadataSource) {
        Assert.notNull(securityMetadataSource, "securityMetadataSource must be not null");
        this.securityMetadataSource = securityMetadataSource;
        // CustomWebSecurityConfigurerAdapter的@EnableGlobalMethodSecurity注解prePostEnabled=true,securedEnabled=true,jsr250Enabled=true已经开启下面所有的投票器
        /*
        // 要使用的投票器与权限决策器
        // 对于方法调用授权,在全局方法安全配置类里,可以看到给MethodSecurityInterceptor默认配置的有
        // RoleVoter、AuthenticatedVoter、Jsr250Voter、和 PreInvocationAuthorizationAdviceVoter,
        // 其中 Jsr250Voter、PreInvocationAuthorizationAdviceVoter 都需要打开指定的开关,才会添加支持

        List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(
                // 基于角色的投票器,基于任何一个以“ROLE_”开头的配置属性进行投票
                new RoleVoter(),
                // 根据认证对象的级别进行投票,具体查询完整认证用户、记住我认证或匿名认证
                new AuthenticatedVoter(),
                // 使用SpEL表达式的投票器,允许我们使用SpEL(Spring Expression Language)去授权使用@PreAuthorize注解的请求
                new WebExpressionVoter(),
                // 支持@RolesAllowed注解等投票器
                new Jsr250Voter()
                // new PreInvocationAuthorizationAdviceVoter() // 基于方法切面的投票器
        );
        accessDecisionManager = new UnanimousBased(decisionVoters);
        */
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
