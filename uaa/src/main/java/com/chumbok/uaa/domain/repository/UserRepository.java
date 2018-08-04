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
     * @param domain
     * @param email
     * @return boolean true/false
     */
    @Query("select count(user) > 0  from User user where user.domain = ?1 and user.email = ?2")
    boolean isExist(String domain, String email);

    /**
     * Retrieve User based on domain and email.
     *
     * @param domain
     * @param email
     * @return User entity
     */
    @Query("select user  from User user where user.domain = ?1 and user.email = ?2")
    User findByDomainAndEmail(String domain, String email);
}
