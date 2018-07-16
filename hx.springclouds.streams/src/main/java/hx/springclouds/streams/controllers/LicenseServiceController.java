package hx.springclouds.streams.controllers;


import hx.springclouds.streams.clients.OrganizationRestTemplateClient;
import hx.springclouds.streams.models.License;
import hx.springclouds.streams.models.OrganizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/organizations")
public class LicenseServiceController {

    @Autowired
    OrganizationRestTemplateClient orgClient;

    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public License getLicense(@PathVariable("organizationId") String orgnizationId) {

        License license = CreateLicense(orgnizationId);
        return license;

    }

    private License CreateLicense(String orgId) {
        OrganizationResponse org=orgClient.GetOrganization(orgId);

        License license = new License();
        license.setName(org.getName());
        return license;
    }
}
