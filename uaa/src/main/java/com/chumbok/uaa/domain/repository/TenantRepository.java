package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Tenant repository.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {

    Page<Tenant> findAllByOrgId(String orgId, Pageable pageable);

    @Query("SELECT t.id FROM Tenant t WHERE t.org.id = ?1")
    List<String> findTenantIdsByOrgId(String orgId);

    @Query("SELECT count(t) > 0 FROM Tenant t WHERE t.org.id = ?1 AND t.tenant = ?2")
    boolean isTenantExist(String orgId, String tenant);

    boolean existsByOrgOrgAndTenant(String org, String tenant);

    void deleteAllByOrgId(String orgId);

    Tenant getByTenant(String tenant);
}
