package hx.springclouds.streams.interceptors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class UserContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("进入 user context filter 针对于组织机构服务");
        HttpServletRequest httpServletRequest=(HttpServletRequest)request;

        logger.debug("获取到组织机构token:",httpServletRequest.getHeader(UserContext.AUTH_TOKEN));

        String correlationId=httpServletRequest.getHeader(UserContext.CORRELATION_ID);
        String userId=httpServletRequest.getHeader(UserContext.USER_ID);
        String authToken=httpServletRequest.getHeader(UserContext.AUTH_TOKEN);
        String orgId=httpServletRequest.getHeader(UserContext.ORG_ID);

        logger.debug("退出UserContextFilter");
        chain.doFilter(request,response);

    }

    @Override
    public void destroy() {

    }
}
