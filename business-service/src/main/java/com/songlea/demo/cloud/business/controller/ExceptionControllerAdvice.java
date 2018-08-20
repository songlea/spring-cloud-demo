package com.songlea.demo.cloud.business.controller;

import com.songlea.demo.cloud.business.config.LocaleMessageSourceConfig;
import com.songlea.demo.cloud.business.model.ErrorResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 统一的异常处理类
 * 即把@ControllerAdvice注解内部使用@ExceptionHandler、@InitBinder、@ModelAttribute注解的方法
 * 应用到所有的 @RequestMapping注解的方法。
 *
 * @author Song Lea
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Resource
    private LocaleMessageSourceConfig localeMessageSourceConfig;

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponseData httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的方式不对(POST/GET/PUT/DELETE...) 接口地址：{}, Exception:{}", url, ex.getMessage());
        return new ErrorResponseData(url, localeMessageSourceConfig.getMessage("EXCEPTION_METHOD_NOT_SUPPORTED"));
    }

    @ResponseBody
    @ExceptionHandler(ServletRequestBindingException.class)
    public ErrorResponseData servletRequestBindingExceptionHandler(ServletRequestBindingException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的参数不完整 接口地址：{}, Exception:{}", url, ex.getMessage());
        return new ErrorResponseData(url, localeMessageSourceConfig.getMessage("EXCEPTION_LACK_PARAMETER"));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponseData methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String url = getRequestUri();
        LOGGER.error("请求方法参数格式不匹配 接口地址：{}, Exception:{}", url, ex.getMessage());
        return new ErrorResponseData(url, localeMessageSourceConfig.getMessage("EXCEPTION_ARGUMENT_TYPE_MISMATCH"));
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponseData httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的MIME类型不支持 接口地址：{}, Exception:{}", url, ex.getMessage());
        return new ErrorResponseData(url, localeMessageSourceConfig.getMessage("EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED"));
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ErrorResponseData httpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的MINE类型不接受 接口地址：{}, Exception:{}", url, ex.getMessage());
        return new ErrorResponseData(url, localeMessageSourceConfig.getMessage("EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE"));
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ErrorResponseData defaultExceptionHandler(Exception ex) {
        String url = getRequestUri();
        LOGGER.error("代码出现异常 接口地址：" + url, ex);
        return new ErrorResponseData(url, localeMessageSourceConfig.getMessage("EXCEPTION_SYSTEM_BUSY"));
    }

    // 取默认的请求
    private String getRequestUri() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes != null) {
            HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
            return httpServletRequest.getRequestURI();
        }
        return null;
    }
}
