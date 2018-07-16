package hx.springclouds.streams.clients;


import hx.springclouds.streams.models.OrganizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {

    @Autowired
    RestTemplate restTemplate;

    public OrganizationResponse GetOrganization(String organizationId){
        ResponseEntity<OrganizationResponse> restExchange=restTemplate.exchange("http://organizationservice/v1/organizations/{organizationId}",
                HttpMethod.GET,
                null, OrganizationResponse.class, organizationId);
        return restExchange.getBody();
    }
}
