package com.songlea.demo.cloud.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 微服务注册服务端
 *
 * @author Song Lea
 */
@SpringBootApplication
@EnableEurekaServer
public class RegistryServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RegistryServiceApplication.class, args);
    }

    @Bean
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return (WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>) configurableServletWebServerFactory -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
            ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/403.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            configurableServletWebServerFactory.addErrorPages(error401Page, error403Page, error404Page, error500Page);
        };
    }

    @Configuration
    @EnableWebSecurity
    public static class SecuritySecureConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().authenticated().and()
                    .httpBasic().and().csrf().disable();
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().antMatchers("/**.html", "/**.css", "/img/**", "/**.js", "/third-party/**");
        }
    }

    @EventListener
    public void listeningEvent(EurekaInstanceCanceledEvent eurekaInstanceCanceledEvent) {
        /*
        监听eureka服务中心的一些状态(@EventListener)：
            EurekaInstanceCanceledEvent：服务下线事件
            EurekaInstanceRegisteredEvent：服务注册事件
            EurekaInstanceRenewedEvent：服务续约事件
            EurekaRegistryAvailableEvent：Eureka注册中心启动事件
            EurekaServerStartedEvent：Eureka Server启动事件
        */
        String appName = eurekaInstanceCanceledEvent.getAppName();
        String serverId = eurekaInstanceCanceledEvent.getServerId();
        LOGGER.info("服务注册中心-【{}{}】服务下线了！", appName, serverId);
    }
}