package com.songlea.demo.cloud.zuul.config;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.repository.DefaultRateLimiterErrorHandler;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.repository.RateLimiterErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zuul限流配置
 *
 * @author Song Lea
 */
@Configuration
public class RateLimitConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitConfig.class);

    @Bean
    public RateLimiterErrorHandler rateLimitErrorHandler() {
        // 对限流运行中的异常进行处理
        return new DefaultRateLimiterErrorHandler() {
            @Override
            public void handleSaveError(String key, Exception e) {
                LOGGER.error("zuul网关限流保存数据时异常！key: " + key, e);
            }

            @Override
            public void handleFetchError(String key, Exception e) {
                LOGGER.error("zuul网关限流取数据时异常！key: " + key, e);
            }

            @Override
            public void handleError(String msg, Exception e) {
                LOGGER.error("zuul网关限流处理异常！msg: " + msg, e);
            }
        };
    }
}