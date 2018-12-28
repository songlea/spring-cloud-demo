package com.songlea.demo.cloud.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.songlea.demo.cloud.security.auth.RestAuthenticationEntryPoint;
import com.songlea.demo.cloud.security.auth.ajax.AjaxAuthenticationProvider;
import com.songlea.demo.cloud.security.auth.ajax.AjaxLoginAuthenticationProcessingFilter;
import com.songlea.demo.cloud.security.auth.jwt.JwtAuthenticationProvider;
import com.songlea.demo.cloud.security.auth.jwt.JwtTokenAuthenticationProcessingFilter;
import com.songlea.demo.cloud.security.auth.jwt.SkipPathRequestMatcher;
import com.songlea.demo.cloud.security.auth.jwt.extractor.TokenExtractor;
import com.songlea.demo.cloud.security.auth.userdetails.ExtendUserDetailsService;
import com.songlea.demo.cloud.security.controller.JwtTokenController;
import com.songlea.demo.cloud.security.filter.CustomCorsFilter;
import com.songlea.demo.cloud.security.filter.CustomInvocationSecurityMetadataSource;
import com.songlea.demo.cloud.security.userdetails.CustomUnanimousBased;
import com.songlea.demo.cloud.security.userdetails.CustomUserRoleVoter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
// prePostEnabled:确定 Spring Security前置注解[@PreAuthorize,@PostAuthorize,..] 是否应该启用
// securedEnabled:确定 Spring Security 安全注解[@Secured] 是否应该启用
// jsr250Enabled:确定 JSR-250注解[@RolesAllowed..] 是否应该启用
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private AjaxAuthenticationProvider ajaxAuthenticationProvider;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExtendUserDetailsService extendUserDetailsService;

    // @Bean
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

    // @Bean
    public FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource(
            FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource) {
        // 访问资源即url时，会通过AbstractSecurityInterceptor拦截器拦截，其中会调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限，
        // 在调用授权管理器AccessDecisionManager，这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，
        // 还会获取被拦截的url和被拦截url所需的全部权限，然后根据所配的策略(一票决定，一票否定，少数服从多数等)，如果权限足够，则返回，权限不够则报错并调用权限不足页面。
        return new CustomInvocationSecurityMetadataSource(filterInvocationSecurityMetadataSource, extendUserDetailsService);
    }

    /*
    AuthenticationManager：用户认证的管理类，所有的认证请求（比如login）都会通过提交一个token给AuthenticationManager的authenticate()方法来实现。
        当然事情肯定不是它来做，具体校验动作会由AuthenticationManager将请求转发给具体的实现类来做。根据实现反馈的结果再调用具体的Handler来给用户以反馈。
        AuthenticationManager默认的实现类是ProviderManager。
    AuthenticationProvider：认证的具体实现类，一个provider是一种认证方式的实现，比如提交的用户名密码我是通过和DB中查出的user记录做比对实现的，那就有一个DaoProvider。
        AuthenticationManager只是一个代理接口，真正的认证就是由AuthenticationProvider来做的。一个AuthenticationManager可以包含多个Provider，
        每个provider通过实现一个support方法来表示自己支持那种Token的认证。
    UserDetailService：用户认证通过Provider来做，所以Provider需要拿到系统已经保存的认证信息，获取用户信息的接口spring-security抽象成UserDetailService。
    AuthenticationToken：所有提交给AuthenticationManager的认证请求都会被封装成一个Token的实现，比如最容易理解的UsernamePasswordAuthenticationToken。
    SecurityContext：当用户通过认证之后，就会为这个用户生成一个唯一的SecurityContext，里面包含用户的认证信息Authentication。
        通过SecurityContext我们可以获取到用户的标识Principle和授权信息GrantedAuthrity。在系统的任何地方只要通过SecurityHolder.getSecurityContext()就可以获取到SecurityContext。
     */
    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return extendUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> permitAllEndpointList = Arrays.asList(JwtTokenController.AUTHENTICATION_URL, JwtTokenController.REFRESH_TOKEN_URL);
        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                // .accessDeniedHandler(new AccessDeniedHandlerImpl())

                // Session设置
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).enableSessionUrlRewriting(false)

                // 权限设置
                .and().authorizeRequests()
                // 登录界面不需要验证
                .antMatchers(permitAllEndpointList.toArray(new String[0])).permitAll()
                .antMatchers("/sys-user/**").permitAll()
                .anyRequest().authenticated()

                //.accessDecisionManager(accessDecisionManager())
                // 与accessDecisionManager不一样，ExpressionUrlAuthorizationConfigurer 并没有提供set方法设置FilterSecurityInterceptor的FilterInvocationSecurityMetadataSource，
                // 可以使用一个扩展方法withObjectPostProcessor，通过该方法自定义一个处理FilterSecurityInterceptor类型的ObjectPostProcessor就可以修改FilterSecurityInterceptor
                /*.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setRejectPublicInvocations(true);
                        object.setSecurityMetadataSource(filterInvocationSecurityMetadataSource(object.getSecurityMetadataSource()));
                        return object;
                    }
                })*/

                // 匿名设置,默认的过滤器为AnonymousAuthenticationFilter
                .and().anonymous().principal("anonymousUser").authorities(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))

                // 登录设置,登录成功处理默认为SavedRequestAwareAuthenticationSuccessHandler,登录失败处理默认的为SimpleUrlAuthenticationFailureHandler
                /*
                .and().formLogin().permitAll()
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler()
                     // 此处为返回一个登录成功的json,而默认的SavedRequestAwareAuthenticationSuccessHandler会重定向到保存的url中
                    (request, response, authentication) -> {
                        LOGGER.info("【{}】登录成功", authentication.getName());
                        ResultData resultData = new ResultData<>(HttpStatus.OK.value(), localeMessageSourceConfig.getMessage(ResultData.LOGIN_SUCCESS), null);
                        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                        response.getWriter().write(JSON.toJSONString(resultData));
                     }
                )
                .failureHandler((request, response, exception) -> {
                    LOGGER.error("登录失败", exception);
                    ResultData resultData = new ResultData<>(HttpStatus.UNAUTHORIZED.value(),
                            localeMessageSourceConfig.getMessage(ResultData.LOGIN_FAILURE), null);
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.getWriter().write(JSON.toJSONString(resultData));
                })
                // 登出设置,logoutSuccessUrl默认/login?logout,而默认的logoutSuccessHandler为SimpleUrlLogoutSuccessHandler
                .and().logout().invalidateHttpSession(true).clearAuthentication(true).permitAll()
                */
                // 过滤器配置
                .and().addFilterBefore(new CustomCorsFilter(), UsernamePasswordAuthenticationFilter.class)
                // 用户或密码的POST请求登录验证(ajax的POST请求)
                .addFilterBefore(new AjaxLoginAuthenticationProcessingFilter(JwtTokenController.AUTHENTICATION_URL, authenticationManager,
                        successHandler, failureHandler, objectMapper), UsernamePasswordAuthenticationFilter.class)
                // jwt请求验证
                .addFilterBefore(new JwtTokenAuthenticationProcessingFilter(authenticationManager, failureHandler, tokenExtractor,
                                new SkipPathRequestMatcher(permitAllEndpointList, JwtTokenController.API_ROOT_URL)),
                        UsernamePasswordAuthenticationFilter.class);
        /*
        final类HttpSecurity常用方法与说明：
           1、openidLogin()：用于基于 OpenId 的验证。0
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
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(ajaxAuthenticationProvider)
                .authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        // 忽略静态资源
        web.ignoring().antMatchers("/resources/**", "/**.html", "/**.css", "/img/**", "/**.js",
                "/third-party/**", "/assets/**", "/images/**", "/**/*.jsp", "/**/favicon.ico");
    }

}
