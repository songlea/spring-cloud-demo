package com.songlea.demo.cloud.gateway.filter;

import com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * 通用POST类型过滤器：该类型的filters在ROUTING返回Response后执行，
 * 用来实现对Response结果进行修改，收集统计数据以及把Response传输会客户端。
 *
 * @author Song Lea
 */
@Component
public class CommonPostFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonPreFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
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
        LOGGER.info("智能路由/服务网关中心请求的URL【{}】PRE验证通过后进入Post过滤器！",
                ctx.getRequest().getRequestURI());
        // 后期可以待实现的处理逻辑(可用来修改响应数据)
        return null;
    }
}
