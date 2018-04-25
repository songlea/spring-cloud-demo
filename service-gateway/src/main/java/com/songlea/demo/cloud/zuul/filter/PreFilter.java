package com.songlea.demo.cloud.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 利用Zuul的过滤器,可以实现对外服务的安全控制,Zuul定义了四种不同生命周期的过滤器类型：
 * PRE：该类型的filters在Request routing到源web-service之前执行,用来实现Authentication、选择源服务地址等
 * ROUTING：该类型的filters用于把Request routing到源web-service，源web-service是实现业务逻辑的服务
 * POST：该类型的filters在ROUTING返回Response后执行,用来实现对Response结果进行修改，收集统计数据以及把Response传输会客户端
 * ERROR：上面三个过程中任何一个出现错误都交由ERROR类型的filters进行处理
 *
 * @author Song Lea
 */
@Component
public class PreFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreFilter.class);

    @Override
    // 过滤器类型
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    // 通过int值来定义过滤器的执行顺序,优先按照order从小到大执行(需要先执行限流过滤器)
    public int filterOrder() {
        return 0;
    }

    @Override
    // 设置该过滤器总是生效，即总是执行拦截请求
    public boolean shouldFilter() {
        return true;
    }

    // 过滤器的具体逻辑
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        LOGGER.info(String.format("收到 %s 请求 %s", request.getMethod(), request.getRequestURL().toString()));
        // 取请求参数中的token
        Object accessToken = request.getParameter("token");
        if (accessToken == null) {
            // 令zuul过滤该请求，不对其进行路由
            ctx.setSendZuulResponse(false);
            ctx.getResponse().setContentType("text/html;charset=UTF-8");
            ctx.setResponseStatusCode(200);
            ctx.setResponseBody("权限不足！");
            return null;
        }
        LOGGER.info("PreFilter中token验证通过，进入下一个过滤器！");
        return null;
    }
}
