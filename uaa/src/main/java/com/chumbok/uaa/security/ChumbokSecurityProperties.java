package com.chumbok.uaa.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Data class to read security properties from application yaml.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.chumbok.security")
public class ChumbokSecurityProperties {

    /**
     * Flag to enable Chumbok Security.
     */
    private boolean enable;

    /**
     * assertOrgWith is a String which going to be validated with access token org.
     */
    private String assertOrgWith;

    /**
     * Flag to check Tenant.
     */
    private boolean assertTenant;

    /**
     * assertTenantWith is a String which going to be validated with access token tenant.
     */
    private String assertTenantWith;

}
