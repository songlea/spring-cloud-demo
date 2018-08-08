package com.songlea.demo.cloud.gateway.fallback;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;

/**
 * 路由发起请求失败时的回滚处理(hystrix熔断)
 *
 * @author Song Lea
 */
@Component
public class ServerFallback implements FallbackProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerFallback.class);

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        LOGGER.error("智能路由/服务网关中心Route【" + route + "】服务不可用(进入熔断)！", cause);

        // 重新输出异常信息(hystrix熔断处理)
        return new ClientHttpResponse() {

            @NonNull
            @Override
            public InputStream getBody() {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
                jsonObject.put("status", 500);
                if (cause != null && cause.getCause() != null)
                    jsonObject.put("error", cause.getCause().getMessage());
                jsonObject.put("message", "The service is not available, please try again later!");
                jsonObject.put("route", route);
                return new ByteArrayInputStream(jsonObject.toJSONString().getBytes(Charsets.UTF_8));
            }

            @NonNull
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                // 和body中的内容编码一致否则容易乱码
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return headers;
            }

            @NonNull
            @Override
            public HttpStatus getStatusCode() {
                // 网关向api服务请求是失败但是消费者客户端向网关发起的请求是OK的,不把api的404,500等问题抛给客户端
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() {
                return HttpStatus.OK.value();
            }

            @NonNull
            @Override
            public String getStatusText() {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {
            }
        };
    }
}
