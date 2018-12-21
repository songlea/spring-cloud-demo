package com.songlea.demo.cloud.security.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RestAuthenticationEntryPoint
 * AuthenticationEntryPoint:用来解决匿名用户访问无权限资源时的异常
 * AccessDeniedHandler 用来解决认证过的用户访问无权限资源时的异常
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /*
    ExceptionTranslationFilter:Spring Security的核心filter之一,用来处理AuthenticationException和AccessDeniedException两种异常.
        AuthenticationException指的是未登录状态下访问受保护资源;
        AccessDeniedException指的是登陆了但是由于权限不足(比如普通用户访问管理员界面)
    ExceptionTranslationFilter 持有两个处理类,分别是AuthenticationEntryPoint和AccessDeniedHandler.
        AccessDeniedHandler:默认实现是 AccessDeniedHandlerImpl, 该类对异常的处理是返回403错误码
        AuthenticationEntryPoint:默认实现是 LoginUrlAuthenticationEntryPoint, 该类的处理是转发或重定向到登录页面
        规则1. 如果异常是 AuthenticationException，使用 AuthenticationEntryPoint 处理;
        规则2. 如果异常是 AccessDeniedException 且用户是匿名用户，使用 AuthenticationEntryPoint 处理;
        规则3. 如果异常是 AccessDeniedException 且用户不是匿名用户，交给 AccessDeniedHandler 处理。
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }

}
