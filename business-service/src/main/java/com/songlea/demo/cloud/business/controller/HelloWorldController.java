package com.songlea.demo.cloud.business.controller;

import com.songlea.demo.cloud.business.config.LocaleMessageSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 业务层服务测试Controller
 *
 * @author Song Lea
 */
@RestController
@RequestMapping("/business")
// 只有加了此注解才能被/actuator/bus-refresh更新
@RefreshScope
public class HelloWorldController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldController.class);

    @Resource
    private LocaleMessageSourceConfig localeMessageSourceConfig;

    // 配置项放在在git服务器上(business-service-dev.yml)
    @Value("${business-service.demo.msg}")
    private String msg;

    @RequestMapping(value = "/git/test", method = RequestMethod.GET)
    public String hellWorld() {
        LOGGER.info("测试git服务器获取配置【business-service.demo.msg: {}】", msg);
        return msg;
    }

    @RequestMapping(value = "/i18n/test", method = RequestMethod.GET)
    public String changeSessionLanguage(
            @RequestHeader(value = "accept-language", required = false) String acceptLanguage) {
        LOGGER.info("请求头【accept-language:{}】", acceptLanguage);
        /*
        // 会话区域解析器之SessionLocaleResolver或Cookie区域解析器之CookieLocaleResolver可以动态设置Local
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (language != null && localeResolver != null) {
            switch (language) {
                case "zh":
                    // 默认的区域解析器之AcceptHeaderLocaleResolver不能动态设置Local,而通过accept-language请求头来解析区域的
                    localeResolver.setLocale(request, response, new Locale("zh", "CN"));
                    break;
                case "en":
                    localeResolver.setLocale(request, response, new Locale("en", "US"));
                    break;
                default:
                    break;
            }
        }
        */
        String message = localeMessageSourceConfig.getMessage("info");
        LOGGER.info("测试国际化配置【info: {}】", message);
        return message;
    }
}