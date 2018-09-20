package com.chumbok.uaa.conf;


import com.chumbok.security.config.AbstractSecurityConfig;
import com.chumbok.security.properties.SecurityProperties;
import com.chumbok.security.util.AuthTokenParser;
import com.chumbok.security.util.EncryptionKeyUtil;
import com.chumbok.uaa.security.AuthenticationHandler;
import com.chumbok.uaa.security.ChumbokSecurityProperties;
import com.chumbok.uaa.security.DefaultAuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Application security config.
 */
@EnableWebSecurity
public class SecurityConfig extends AbstractSecurityConfig {

    private ChumbokSecurityProperties chumbokSecurityProperties;
    private EncryptionKeyUtil encryptionKeyUtil;
    private String authTokenSigningKeyPath;

    /**
     * Construct security config with userDetailService, encryptionKeyUtil and authTokenSigningKeyPath.
     *
     * @param userDetailsService
     * @param encryptionKeyUtil
     * @param authTokenSigningKeyPath
     */
    public SecurityConfig(UserDetailsService userDetailsService,
                          EncryptionKeyUtil encryptionKeyUtil,
                          ChumbokSecurityProperties chumbokSecurityProperties,
                          @Value("${com.chumbok.auth.token-signing-public-key-path}") String authTokenSigningKeyPath) {
        super.setUserDetailsService(userDetailsService);
        this.encryptionKeyUtil = encryptionKeyUtil;
        this.chumbokSecurityProperties = chumbokSecurityProperties;
        this.authTokenSigningKeyPath = authTokenSigningKeyPath;
    }

    /**
     * Creates authenticationManager bean.
     *
     * @return
     * @throws Exception
     */
    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Creates default AuthenticationHandler bean.
     *
     * @return
     */
    @Bean
    protected AuthenticationHandler authenticationHandler() {
        return new DefaultAuthenticationHandler();
    }

    /**
     * Create AuthTokenParser bean.
     *
     * @return
     */
    @Bean
    public AuthTokenParser authTokenParser() {
        return new AuthTokenParser(encryptionKeyUtil.loadPublicKey(authTokenSigningKeyPath));
    }

    /**
     * Set AuthTokenParser to super class so that auth token can be consumed.
     * @param authTokenParser
     */
    @Autowired
    @Override
    protected void setAuthTokenParser(AuthTokenParser authTokenParser) {
        super.setAuthTokenParser(authTokenParser);
    }

    /**
     * Create SecurityProperties bean.
     *
     * @return
     */
    @Bean
    public SecurityProperties securityProperties() {
        return SecurityProperties.builder()
                .enable(chumbokSecurityProperties.isEnable())
                .assertOrgWith(chumbokSecurityProperties.getAssertOrgWith())
                .assertTenant(chumbokSecurityProperties.isAssertTenant())
                .assertTenantWith(chumbokSecurityProperties.getAssertTenantWith())
                .build();
    }

    /**
     * Set SecurityProperties to super class so that auth token can be consumed.
     * @param securityProperties
     */
    @Autowired
    @Override
    protected void setSecurityProperties(SecurityProperties securityProperties) {
        super.setSecurityProperties(securityProperties);
    }


}
