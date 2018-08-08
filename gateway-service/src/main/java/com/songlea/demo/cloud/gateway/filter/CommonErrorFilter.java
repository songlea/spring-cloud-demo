package com.songlea.demo.cloud.gateway.filter;

import com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * 通用错误处理过滤器
 *
 * @author Song Lea
 */
@Component
public class CommonErrorFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonPreFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
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
        LOGGER.info("智能路由/服务网关中心请求的URL【{}】验证出现异常后进入Error过滤器！",
                ctx.getRequest().getRequestURI());
        // 后期可以待实现的处理逻辑(前面Filter发生异常时调用)
        return null;
    }
}
