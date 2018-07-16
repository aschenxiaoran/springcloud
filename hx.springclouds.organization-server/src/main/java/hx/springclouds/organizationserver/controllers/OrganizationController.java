package hx.springclouds.organizationserver.controllers;


import hx.springclouds.organizationserver.models.OrganizationResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/organizations")
public class OrganizationController {

    @RequestMapping(value = "/{organizationId}",method = RequestMethod.GET)
    public OrganizationResponse GetOrganization(@PathVariable("organizationId") String organizationId) {
        OrganizationResponse org = new OrganizationResponse();
        org.setName("xiaoran");
        return org;
    }
}
