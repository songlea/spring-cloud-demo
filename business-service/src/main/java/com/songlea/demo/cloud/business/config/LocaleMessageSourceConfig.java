package com.songlea.demo.cloud.business.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * 获取国际化信息
 *
 * @author Song Lea
 */
@Configuration
public class LocaleMessageSourceConfig {

    /*
    // 默认的AcceptHeaderLocaleResolver不支持动态设置Local,这里可以指定使用SessionLocaleResolver或CookieLocaleResolver
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        // 设置默认区域
        sessionLocaleResolver.setDefaultLocale(Locale.CHINA);
        return sessionLocaleResolver;
    }
    */

    @Resource
    private MessageSource messageSource;

    /**
     * @param key :对应messages配置的key
     */
    public String getMessage(String key) {
        return getMessage(key, null);
    }

    /**
     * @param key  :对应messages配置的key.
     * @param args : 数组参数.
     */
    public String getMessage(String key, Object[] args) {
        return getMessage(key, args, "");
    }

    /**
     * @param key            :对应messages配置的key.
     * @param args           : 数组参数.
     * @param defaultMessage : 没有设置key的时候的默认值.
     */
    public String getMessage(String key, Object[] args, String defaultMessage) {
        // 这里使用比较方便的方法，不依赖request.
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }
}
