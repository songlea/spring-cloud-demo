package com.songlea.demo.cloud.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder:密码加密接口
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    protected PasswordEncoder passwordEncoder() {
        // 推荐使用实现类BCryptPasswordEncoder、SCryptPasswordEncoder与Pbkdf2PasswordEncoder
        return new BCryptPasswordEncoder();
    }
}
