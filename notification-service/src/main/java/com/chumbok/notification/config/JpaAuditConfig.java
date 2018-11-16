package com.chumbok.notification.config;

import com.chumbok.security.util.SecurityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableJpaAuditing
public class JpaAuditConfig {

    private final SecurityUtil securityUtil;

    /**
     * Construct JpaAuditConfig with SecurityUtil.
     *
     * @param securityUtil
     */
    public JpaAuditConfig(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl(securityUtil);
    }

    @Bean
    public AuditingEntityListener createAuditingListener() {
        return new AuditingEntityListener();
    }

    public static class AuditorAwareImpl implements AuditorAware<String> {

        private final SecurityUtil securityUtil;

        public AuditorAwareImpl(SecurityUtil securityUtil) {
            this.securityUtil = securityUtil;
        }

        @Override
        public Optional<String> getCurrentAuditor() {
            return Optional.of(securityUtil.getAuthenticatedUser().get().getUsername());
        }
    }
}
