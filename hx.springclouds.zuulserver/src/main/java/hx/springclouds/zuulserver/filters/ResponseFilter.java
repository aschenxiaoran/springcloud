package hx.springclouds.zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseFilter extends ZuulFilter {
    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Override
    public String filterType() {
        return filterUtils.POST_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext=RequestContext.getCurrentContext();
        logger.error("添加corelation id 到出栈头部中{}",filterUtils.getCorrelationId());

        requestContext.getResponse().addHeader(filterUtils.CORRELATIONID,filterUtils.getCorrelationId());

        logger.error("完成出栈请求时{}",requestContext.getRequest().getRequestURI());

        return null;
    }
}
