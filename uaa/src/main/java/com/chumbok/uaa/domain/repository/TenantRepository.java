package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Tenant repository.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {


    Page<Tenant> findAllByOrgId(String orgId, Pageable pageable);

    Optional<Tenant> findByOrgIdAndId(String orgId, String tenantId);

    @Query("SELECT count(t) > 0 from Tenant t WHERE t.org.id = ?1 AND t.tenant = ?2")
    boolean existsByOrgIdAndTenant(String orgId, String tenant);


    void deleteAllByOrgId(String orgId);

    List<Tenant> findAllByOrgId(String orgId);
}
