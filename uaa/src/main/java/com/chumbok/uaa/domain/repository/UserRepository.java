package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {


}
