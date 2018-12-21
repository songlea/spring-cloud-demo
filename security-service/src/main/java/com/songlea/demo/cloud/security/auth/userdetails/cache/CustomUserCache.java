package com.songlea.demo.cloud.security.auth.userdetails.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * UserCache(实现UserDetailsService的Redis缓存)：
 * CachingUserDetailsService类的构造接收一个用于真正加载UserDetails的UserDetailsService实现类，当需要加载UserDetails时，
 * 其首先会从缓存中获取，如果缓存中没有对应的UserDetails存在，则使用持有的UserDetailsService实现类进行加载，
 * 然后将加载后的结果存放在缓存中，UserDetails与缓存的交互是通过UserCache接口来实现的。
 *
 * @author Song Lea
 */
@Component
public class CustomUserCache implements UserCache {

    private static final String USER_CACHE_PREFIX = "security-service_";
    private static final long USER_CACHE_TIME = 30 * 60;

    private final RedisTemplate<String, UserDetails> userDetailsRedisTemplate;

    @Autowired
    public CustomUserCache(RedisTemplate<String, UserDetails> userDetailsRedisTemplate) {
        Assert.notNull(userDetailsRedisTemplate, "userDetailsRedisTemplate must be not null");
        this.userDetailsRedisTemplate = userDetailsRedisTemplate;
    }

    @Override
    public UserDetails getUserFromCache(String username) {
        // 从缓存中加载
        return userDetailsRedisTemplate.opsForValue().get(USER_CACHE_PREFIX + username);
    }

    @Override
    public void putUserInCache(UserDetails user) {
        // 放入缓存,默认缓存30分钟
        userDetailsRedisTemplate.opsForValue().set(USER_CACHE_PREFIX + user.getUsername(), user,
                USER_CACHE_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void removeUserFromCache(String username) {
        // 删除对应key的缓存(当更改用户的配置的时候,必须删除缓存)
        userDetailsRedisTemplate.delete(USER_CACHE_PREFIX + username);
    }

}
