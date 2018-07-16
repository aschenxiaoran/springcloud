package hx.springclouds.organizationserver.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class ClientInterceptorConfig {

    @Autowired
    UserContextInterceptor userContextInterceptor;

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate=new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(userContextInterceptor));
        return restTemplate;
    }
}
