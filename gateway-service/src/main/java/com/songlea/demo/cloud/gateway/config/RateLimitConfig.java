package com.songlea.demo.cloud.gateway.config;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.repository.DefaultRateLimiterErrorHandler;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.repository.RateLimiterErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Zuul限流配置
 *
 * @author Song Lea
 */
@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimiterErrorHandler rateLimitErrorHandler() {
        
        // 对限流在运行中的异常进行处理
        return new DefaultRateLimiterErrorHandler() {

            @Override
            public void handleSaveError(String key, Exception e) {
                super.handleSaveError(key, e);
            }

            @Override
            public void handleFetchError(String key, Exception e) {
                super.handleFetchError(key, e);
            }

            @Override
            public void handleError(String msg, Exception e) {
                super.handleError(msg, e);
            }
        };
    }
}