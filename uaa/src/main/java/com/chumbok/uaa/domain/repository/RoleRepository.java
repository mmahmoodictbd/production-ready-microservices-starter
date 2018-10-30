package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Role repository.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByRole(String role);
}
