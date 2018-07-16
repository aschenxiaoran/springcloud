package hx.springclouds.organizationserver.interceptors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                        ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers=httpRequest.getHeaders();
        headers.add(UserContext.CORRELATION_ID,UserContextHolder.getUserContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN,UserContextHolder.getUserContext().getAuthToken());

        return clientHttpRequestExecution.execute(httpRequest,bytes);
    }
}
