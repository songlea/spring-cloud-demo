package com.songlea.demo.cloud.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 登录对象
 */
public class LoginRequest {

    private String username;
    private String password;

    // 当json在反序列化时,默认选择类的无参构造函数创建类对象,当没有无参构造函数时会报错.
    // @JsonCreator作用就是指定反序列化时用的无参构造函数,构造方法的参数前面需要加上@JsonProperty,否则会报错
    @JsonCreator
    public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    // @JsonValue 可以用在get方法或者属性字段上,一个类只能用一个,当加上@JsonValue注解时序列化是只返回这一个字段的值
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
