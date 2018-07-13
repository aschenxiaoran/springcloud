package hx.springclouds.streams.controllers;


import hx.springclouds.streams.models.License;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/organizations")
public class LicenseServiceController {

    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public License getLicense(@PathVariable("organizationId") String orgnizationId) {

        License license = CreateLicense();
        return license;

    }

    private License CreateLicense() {
        License license = new License();
        license.setName("xiaoran");
        return license;
    }
}
