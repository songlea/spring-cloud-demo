package com.songlea.demo.cloud.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants;
import com.songlea.demo.cloud.gateway.model.ErrorResponseData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 前置处理过滤器,在请求路由之前调用
 *
 * @author Song Lea
 */
@Component
public class CommonPreFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonPreFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -1;
    } // 最先验证

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        LOGGER.info("智能路由/服务网关中心请求的URL【{}】进入PRE过滤器！", request.getRequestURI());

        // 可以作鉴权、限流、参数加解密、是否继续路由请求等操作
        String hasTokenParam = request.getParameter("token");
        if (StringUtils.isBlank(hasTokenParam)) {
            LOGGER.warn("access token is empty");
            // 过滤该请求,不往下级服务去转发请求
            ctx.setSendZuulResponse(false);
            // output exception
            ctx.setResponseBody(JSON.toJSONString(
                    ErrorResponseData.ExceptionEnum.NO_OPEN_API_TOKEN_HEAD.getResult(request.getRequestURI())));
            ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            ctx.setResponseStatusCode(401);
            ctx.put("IS_OVER", "YES");
            return null;
        }
        LOGGER.info("access token ok");
        return null;
    }
}