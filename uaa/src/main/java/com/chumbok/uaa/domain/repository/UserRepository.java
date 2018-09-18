package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * User repository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if user exist.
     *
     * @param org
     * @param tenant
     * @param username
     * @return boolean true/false
     */
    @Query("select count(user) > 0  from User user where user.org = ?1 and user.tenant = ?2 and user.username = ?3")
    boolean isExist(String org, String tenant, String username);

    /**
     * Retrieve User based on org, tenant and username.
     *
     * @param org
     * @param tenant
     * @param username
     * @return User entity
     */
    @Query("select user  from User user where user.org = ?1 and user.tenant = ?2 and user.username = ?3")
    User find(String org, String tenant, String username);
}
