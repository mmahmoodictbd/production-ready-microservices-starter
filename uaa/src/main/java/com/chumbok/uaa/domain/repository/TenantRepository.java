package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Tenant repository.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

}
