package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User repository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Checks if user exist.
     *
     * @param org      the org
     * @param tenant   the tenant
     * @param username the username
     * @return boolean true/false
     */
    @Query("select count(user) > 0  from User user where user.org.org = ?1 and user.tenant.tenant = ?2 and user.username = ?3")
    boolean isExist(String org, String tenant, String username);

    /**
     * Retrieve User based on org, tenant and username.
     *
     * @param org      the org
     * @param tenant   the tenant
     * @param username the username
     * @return User entity
     */
    @Query("select user  from User user where user.org.org = ?1 and user.tenant.tenant = ?2 and user.username = ?3")
    User find(String org, String tenant, String username);

    /**
     * Find all by org id and tenant id page.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param pageable the pageable
     * @return the page
     */
    Page<User> findAllByOrgIdAndTenantId(String orgId, String tenantId, Pageable pageable);

    /**
     * Find by org id and tenant id and id optional.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param id       the id
     * @return the optional
     */
    Optional<User> findByOrgIdAndTenantIdAndId(String orgId, String tenantId, String id);


    void deleteAllByOrgIdAndTenantId(String orgId, String tenantId);
}
