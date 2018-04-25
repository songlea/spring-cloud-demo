package com.songlea.demo.cloud.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * POST：该类型的filters在ROUTING返回Response后执行,用来实现对Response结果进行修改，收集统计数据以及把Response传输会客户端
 *
 * @author Song Lea
 */
@Component
public class PostFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        // RequestContext ctx = RequestContext.getCurrentContext(); // 取前一个过滤器保存的变量值
        return true;
    }

    @Override
    public Object run() {
        LOGGER.info("进入Post过滤器！");
        return null;
    }
}
