package com.songlea.demo.cloud.security.config;

import com.songlea.demo.cloud.security.filter.CustomInvocationSecurityMetadataSource;
import com.songlea.demo.cloud.security.service.PermissionService;
import com.songlea.demo.cloud.security.userdetails.CustomUnanimousBased;
import com.songlea.demo.cloud.security.userdetails.CustomUserDetailsService;
import com.songlea.demo.cloud.security.userdetails.CustomUserRoleVoter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private PermissionService permissionService; // 用户数据来源

    @Bean
    public PasswordEncoder passwordEncoder() {
        // PasswordEncoder:密码加密接口
        // 推荐使用实现类BCryptPasswordEncoder、SCryptPasswordEncoder与Pbkdf2PasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(
                // 支持表达式投票器,如hasRole("")
                new WebExpressionVoter(),
                // 自定义角色投票器,角色名不要求必须以ROLE_开头
                new CustomUserRoleVoter(),
                // 支持IS_AUTHENTICATED认证
                new AuthenticatedVoter());
        // AffirmativeBased决策器：一票通过，只要有一个投票器通过就允许访问资源
        // UnanimousBased决策器：必须所有投票器都通过
        CustomUnanimousBased customUnanimousBased = new CustomUnanimousBased(decisionVoters);
        // 当所有的投票都弃权后则抛出AccessDeniedException
        customUnanimousBased.setAllowIfAllAbstainDecisions(true);
        return customUnanimousBased;
    }

    @Bean
    public FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource(
            FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
        // 访问资源即url时，会通过AbstractSecurityInterceptor拦截器拦截，其中会调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限，
        // 在调用授权管理器AccessDecisionManager，这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，
        // 还会获取被拦截的url和被拦截url所需的全部权限，然后根据所配的策略(一票决定，一票否定，少数服从多数等)，如果权限足够，则返回，权限不够则报错并调用权限不足页面。
        return new CustomInvocationSecurityMetadataSource(filterInvocationSecurityMetadataSource, permissionService);
    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(permissionService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Since we want the protected resources to be accessible in the UI as well we need
                // session creation to be allowed (it's disabled by default in 2.0.6)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).enableSessionUrlRewriting(false)
                .and().cors()
                .and().authorizeRequests()
                .anyRequest().authenticated().accessDecisionManager(accessDecisionManager())
                // 与accessDecisionManager不一样，ExpressionUrlAuthorizationConfigurer 并没有提供set方法设置FilterSecurityInterceptor的FilterInvocationSecurityMetadataSource，
                // 可以使用一个扩展方法withObjectPostProcessor，通过该方法自定义一个处理FilterSecurityInterceptor类型的ObjectPostProcessor就可以修改FilterSecurityInterceptor
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setRejectPublicInvocations(true);
                        object.setSecurityMetadataSource(filterInvocationSecurityMetadataSource(object.getSecurityMetadataSource()));
                        return object;
                    }
                })
                .and().anonymous().authorities(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
                .and().logout().permitAll().clearAuthentication(true).invalidateHttpSession(true)
                .and().formLogin().permitAll();
        /*
        final类HttpSecurity常用方法与说明：
           1、openidLogin()：用于基于 OpenId 的验证。
           2、headers()：将安全标头添加到响应。
           3、cors()：配置跨域资源共享（ CORS ）。
           4、sessionManagement()：允许配置会话管理。
           5、portMapper()：允许配置一个PortMapper(HttpSecurity#(getSharedObject(class)))，其他提供SecurityConfigurer的对象使用 PortMapper 从 HTTP 重定向到 HTTPS 或者从 HTTPS 重定向到 HTTP。
                默认情况下，Spring Security使用一个PortMapperImpl映射 HTTP 端口8080到 HTTPS 端口8443，HTTP 端口80到 HTTPS 端口443。
           6、jee()：配置基于容器的预认证。 在这种情况下，认证由Servlet容器管理。
           7、x509()：配置基于x509的认证。
           8、rememberMe()：允许配置“记住我”的验证。
           9、authorizeRequests()：允许基于使用HttpServletRequest限制访问。
           10、requestCache()：允许配置请求缓存。
           11、exceptionHandling()：允许配置错误处理。
           12、securityContext()：在HttpServletRequests之间的SecurityContextHolder上设置SecurityContext的管理。 当使用WebSecurityConfigurerAdapter时，这将自动应用。
           13、servletApi()：将HttpServletRequest方法与在其上找到的值集成到SecurityContext中。 当使用WebSecurityConfigurerAdapter时，这将自动应用。
           14、csrf()：添加 CSRF 支持，使用WebSecurityConfigurerAdapter时，默认启用。
           15、logout()：添加退出登录支持。当使用WebSecurityConfigurerAdapter时，这将自动应用。默认情况是，访问URL"/ logout"，使HTTP Session无效来清除用户，清除已配置的任何#rememberMe()身份验证，清除SecurityContextHolder，然后重定向到"/login?success"。
           16、anonymous()：允许配置匿名用户的表示方法。 当与WebSecurityConfigurerAdapter结合使用时，这将自动应用。 默认情况下，匿名用户将使用org.springframework.security.authentication.AnonymousAuthenticationToken表示，并包含角色 "ROLE_ANONYMOUS"。
           17、formLogin()：指定支持基于表单的身份验证。如果未指定FormLoginConfigurer#loginPage(String)，则将生成默认登录页面。
           18、oauth2Login()：根据外部OAuth 2.0或OpenID Connect 1.0提供程序配置身份验证。
           19、requiresChannel()：配置通道安全。为了使该配置有用，必须提供至少一个到所需信道的映射。
           20、httpBasic()：配置 Http Basic 验证。
           21、addFilterAt()：在指定的Filter类的位置添加过滤器。
           22、addFilter()、addFilterBefore()、addFilterAfter()：添加(或指定Filter类的位置前后)过滤器
         */
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        // 忽略静态资源
        web.ignoring().antMatchers("/resources/**", "/**.html", "/**.css", "/img/**", "/**.js",
                "/third-party/**", "/assets/**", "/images/**", "/**/*.jsp", "/**/favicon.ico");
    }

}
