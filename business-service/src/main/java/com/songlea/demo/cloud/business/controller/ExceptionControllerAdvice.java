package com.songlea.demo.cloud.business.controller;

import com.songlea.demo.cloud.business.model.ErrorResponseData;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的方式不对(POST/GET/PUT/DELETE...)  接口地址：" + url, ex);
        getHttpServletResponse(ErrorResponseData.ExceptionEnum.EXCEPTION_METHOD_NOT_SUPPORTED.getResult(url));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public void servletRequestBindingExceptionHandler(ServletRequestBindingException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的参数不完整  接口地址：" + url, ex);
        getHttpServletResponse(ErrorResponseData.ExceptionEnum.EXCEPTION_LACK_PARAMETER.getResult(url));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String url = getRequestUri();
        LOGGER.error("请求方法参数格式不匹配  接口地址：" + url, ex);
        getHttpServletResponse(ErrorResponseData.ExceptionEnum.EXCEPTION_ARGUMENT_TYPE_MISMATCH.getResult(url));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public void httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的MIME类型不支持  接口地址：" + url, ex);
        getHttpServletResponse(ErrorResponseData.ExceptionEnum.EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED.getResult(url));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public void httpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        String url = getRequestUri();
        LOGGER.error("请求的MINE类型不接受  接口地址：" + url, ex);
        getHttpServletResponse(ErrorResponseData.ExceptionEnum.EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE.getResult(url));
    }

    @ExceptionHandler(Exception.class)
    public void defaultExceptionHandler(Exception ex) {
        String url = getRequestUri();
        LOGGER.error("代码出现异常  接口地址：" + url, ex);
        getHttpServletResponse(ErrorResponseData.ExceptionEnum.EXCEPTION_SYSTEM_BUSY.getResult(url));
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

    // 取HttpServletResponse
    private void getHttpServletResponse(ErrorResponseData errorResponseData) {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes != null) {
            HttpServletResponse httpServletResponse = servletRequestAttributes.getResponse();
            if (httpServletResponse != null) {
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                // getWriter()与getOutputStream()不能在一个请求中一起使用
                try (PrintWriter printWriter = httpServletResponse.getWriter()) {
                    printWriter.print(JSON.toJSONString(errorResponseData));
                    printWriter.flush();
                } catch (IOException e) {
                    LOGGER.error("ProxyService服务ControllerAdvice异常！", e);
                }
            }
        }
    }
}
