package com.songlea.demo.cloud.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 配置资源服务器与授权服务器
 */
@Configuration
public class OAuth2ServerConfig {

    private static final String DEMO_RESOURCE_ID = "order";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(DEMO_RESOURCE_ID).stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    // Since we want the protected resources to be accessible in the UI as well we need
                    // session creation to be allowed (it's disabled by default in 2.0.6)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .and()
                    .requestMatchers().anyRequest()
                    .and()
                    .anonymous()
                    .and()
                    .authorizeRequests()
//                    .antMatchers("/product/**").access("#oauth2.hasScope('select') and hasRole('ROLE_USER')")
                    .antMatchers("/order/**").authenticated();//配置order访问控制，必须认证过后才可以访问
            // @formatter:on
        }
    }

    /**
     * 认证服务配置类
     * 下面是配置一个授权服务必须要实现的endpoints：
     * AuthorizationEndpoint：用于服务授权请求,默认URL:/oauth/authorize
     * TokenEndpoint:用于服务访问令牌的请求,默认URL:/oauth/token
     */
    @Configuration
    // 用于配置的OAuth 2.0授权服务器机制
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Resource
        private AuthenticationManager authenticationManager;

        @Resource
        private DataSource dataSource;

        @Bean
        public JdbcClientDetailsService jdbcClientDetailsService() {
            // 对于Client信息,ClientDetailsService接口负责从存储仓库中读取数据,实现类包括JdbcClientDetailsService与InMemoryClientDetailsService
            return new JdbcClientDetailsService(dataSource);
        }

        // 用来配置客户端详情服务(ClientDetailsService),客户端详情(Client details)在这里进行初始化,或指向一个存在的存储
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            /*
            客户端的授权模式(表字段authorized_grant_types):
            1) 授权码模式（authorization code）
                授权码模式（authorization code）是功能最完整、流程最严密的授权模式，它的特点就是通过客户端的后台服务器，与"服务提供商"的认证服务器进行互动。
                （A）用户访问客户端，后者将前者导向认证服务器。
                    e.g.GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom
                    response_type：表示授权类型，必选项，此处的值固定为"code"；
                    client_id：表示客户端的ID，必选项；
                    redirect_uri：表示重定向URI，可选项；
                    scope：表示申请的权限范围，可选项；
                    state：表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值。
                （B）用户选择是否给予客户端授权。
                （C）假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（redirection URI），同时附上一个授权码。
                    code：表示授权码，必选项。该码的有效期应该很短，通常设为10分钟，客户端只能使用该码一次，否则会被授权服务器拒绝。该码与客户端ID和重定向URI，是一一对应关系；
                    state：如果客户端的请求中包含这个参数，认证服务器的回应也必须一模一样包含这个参数。
                （D）客户端收到授权码，附上早先的"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见。
                    e.g:POST /token grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
                    grant_type：表示使用的授权模式，必选项，此处的值固定为"authorization_code"；
                    code：表示上一步获得的授权码，必选项；
                    redirect_uri：表示重定向URI，必选项，且必须与A步骤中的该参数值保持一致；
                    client_id：表示客户端ID，必选项；
                （E）认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（access token）和更新令牌（refresh token）
                    access_token：表示访问令牌，必选项；
                    token_type：表示令牌类型，该值大小写不敏感，必选项，可以是bearer类型或mac类型；
                    expires_in：表示过期时间，单位为秒。如果省略该参数，必须其他方式设置过期时间；
                    refresh_token：表示更新令牌，用来获取下一次的访问令牌，可选项；
                    scope：表示权限范围，如果与客户端申请的范围一致，此项可省略。
            2) 简化模式
                简化模式（implicit grant type）不通过第三方应用程序的服务器，直接在浏览器中向认证服务器申请令牌，跳过了"授权码"这个步骤，因此得名。所有步骤在浏览器中完成，令牌对访问者是可见的，且客户端不需要认证。
            3) 密码模式
                密码模式（Resource Owner Password Credentials Grant）中，用户向客户端提供自己的用户名和密码。客户端使用这些信息，向"服务商提供商"索要授权。
                （A）用户向客户端提供用户名和密码。
                （B）客户端将用户名和密码发给认证服务器，向后者请求令牌。
                    grant_type：表示授权类型，此处的值固定为"password"，必选项。
                    username：表示用户名，必选项。
                    password：表示用户的密码，必选项。
                    scope：表示权限范围，可选项。
                （C）认证服务器确认无误后，向客户端提供访问令牌。
             4) 客户端模式
                客户端模式（Client Credentials Grant）指客户端以自己的名义，而不是以用户的名义，向"服务提供商"进行认证。
                严格地说，客户端模式并不属于OAuth框架所要解决的问题。在这种模式中，用户直接向客户端注册，客户端以自己的名义要求"服务提供商"提供服务，其实不存在授权问题。
                （A）客户端向认证服务器进行身份认证，并要求一个访问令牌。
                    grant_type：表示授权类型，此处的值固定为"client_credentials"，必选项。
                    scope：表示权限范围，可选项
                （B）认证服务器确认无误后，向客户端提供访问令牌。
             */
            clients.withClientDetails(jdbcClientDetailsService());
        }

        // 用来配置授权(authorization)以及令牌(token)的访问端点和令牌服务(token services)
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                    .tokenStore(new InMemoryTokenStore())
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
            //允许表单认证
            oauthServer.allowFormAuthenticationForClients();
        }
    }
}
