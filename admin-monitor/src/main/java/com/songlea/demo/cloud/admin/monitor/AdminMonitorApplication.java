package com.songlea.demo.cloud.admin.monitor;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 通过spring boot admin对集群服务进行监控
 *
 * @author Song Lea
 */
@SpringBootApplication
// The Spring Boot Admin Server can use Spring Clouds DiscoveryClient to discover applications.
// The advantage is that the clients don’t have to include the spring-boot-admin-starter-client.
@EnableEurekaClient
@EnableAdminServer
public class AdminMonitorApplication {

    public static void main(String[] args) {
        // new SpringApplication().addListeners();或SpringApplicationBuilder.listeners(…​);来添加spring监听
        /* spring监听按如下顺序执行：
            1、An ApplicationStartingEvent is sent at the start of a run, but before any processing except the registration of listeners and initializers.
            2、An ApplicationEnvironmentPreparedEvent is sent when the Environment to be used in the context is known, but before the context is created.
            3、An ApplicationPreparedEvent is sent just before the refresh is started, but after bean definitions have been loaded.
            4、An ApplicationReadyEvent is sent after the refresh and any related callbacks have been processed to indicate the application is ready to service requests.
            5、An ApplicationFailedEvent is sent if there is an exception on startup.
        */
        SpringApplication.run(AdminMonitorApplication.class, args);
    }

    @Configuration
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Page with login form is served as /login.html and does a POST on /login
            http.formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll();
            // The UI does a POST on /logout on logout
            http.logout().logoutUrl("/logout");
            // The ui currently doesn't support csrf
            http.csrf().disable();
            // Requests for the login page and the static assets are allowed
            http.authorizeRequests()
                    .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**", "/**/*.js")
                    .permitAll();
            // ... and any other request needs to be authorized
            http.authorizeRequests().antMatchers("/**").authenticated();
            // Enable so that the clients can authenticate via HTTP basic for registering
            http.httpBasic();
        }
    }

    // 对404与500错误界面
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            container.addErrorPages(error404Page, error500Page);
        });
    }

    // spring boot会自动配置spring mvc
    /* The auto-configuration adds the following features on top of Spring’s defaults:
            1、Inclusion of ContentNegotiatingViewResolver and  beans.
                ContentNegotiatingViewResolver：是根据客户提交的MimeType(如text/html,application/xml)来跟服务端的一组viewResover的MimeType相比较,如果符合,即返回viewResover的数据.
                BeanNameViewResolver：视图解析器,使用视图名称来解析视图.
            2、Support for serving static resources, including support for WebJars (see below).
            3、Automatic registration of Converter, GenericConverter, Formatter beans.
            4、Support for HttpMessageConverters (see below).
            5、Automatic registration of MessageCodesResolver (see below).
            6、Static index.html support.
            7、Custom Favicon support (see below).
            8、Automatic use of a ConfigurableWebBindingInitializer bean (see below).
      If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc
     */
}
