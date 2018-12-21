package com.songlea.demo.cloud.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class RedisConfig {

    @Bean(name = "userDetailsRedisTemplate")
    public RedisTemplate<String, UserDetails> userDetailsRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, UserDetails> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        return template;
    }

}
