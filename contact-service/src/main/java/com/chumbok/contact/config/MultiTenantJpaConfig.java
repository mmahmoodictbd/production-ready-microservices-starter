package com.chumbok.contact.config;


import com.chumbok.multitenancy.TenantAware;
import com.chumbok.security.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class MultiTenantJpaConfig {

    private final SecurityUtil securityUtil;

    public MultiTenantJpaConfig(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    @Bean
    public TenantAware<String> tenantAware() {
        return new TenantAwareImpl(securityUtil);
    }

    static class TenantAwareImpl implements TenantAware<String> {

        private final SecurityUtil securityUtil;

        public TenantAwareImpl(SecurityUtil securityUtil) {
            this.securityUtil = securityUtil;
        }

        @Override
        public Optional<String> getOrg() {
            if (securityUtil.getAuthenticatedUser().isPresent()) {
                return Optional.of(securityUtil.getAuthenticatedUser().get().getOrg());
            } else {
                return Optional.empty();
            }

        }

        @Override
        public Optional<String> getTenant() {
            if (securityUtil.getAuthenticatedUser().isPresent()) {
                return Optional.of(securityUtil.getAuthenticatedUser().get().getTenant());
            } else {
                return Optional.empty();
            }
        }
    }

}