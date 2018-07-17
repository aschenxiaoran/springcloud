package hx.springclouds.zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

//前置过滤器
@Component
public class TrackingFilter extends ZuulFilter {

    private static final int FILTER_ORDER=1;
    private static final boolean SHOULD_FILTER=true;
    private static final Logger logger=LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Override
    public String filterType() {
        return filterUtils.PRE_FILTER_TYPE;
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
        if(isCorrelationIdPresent()){
            logger.error("tmx-correlation-id found in tracking filter: {}. ", filterUtils.getCorrelationId());
        }
        else {
            filterUtils.setCorrelationId(generateCorrelationId());
            logger.error("tmx-correlation-id in tracking filter:{}",filterUtils.getCorrelationId());
        }

        RequestContext requestContext=RequestContext.getCurrentContext();
        logger.error("处理请求的进程是{}",requestContext.getRequest().getRequestURI());
        return null;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    private boolean isCorrelationIdPresent() {
        return filterUtils.getCorrelationId()!=null;
    }
}
