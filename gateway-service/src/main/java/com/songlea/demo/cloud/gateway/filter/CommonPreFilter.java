package com.songlea.demo.cloud.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

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
        return ZuulFilterConstants.DEFAULT_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        LOGGER.info("智能路由/服务网关中心请求的URL【{}】进入PRE过滤器！", ctx.getRequest().getRequestURI());
        // 可以作鉴权、限流、参数加解密、是否继续路由请求等操作
        return null;
    }
}