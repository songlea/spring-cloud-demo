package com.songlea.demo.cloud.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * ERROR：上面三个过程中任何一个出现错误都交由ERROR类型的filters进行处理
 */
@Component
public class ErrorFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        LOGGER.info("进入异常过滤器！");
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseBody("出现异常！");
        return null;
    }
}
