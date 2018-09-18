package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.domain.model.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Org repository.
 */
@Repository
public interface OrgRepository extends JpaRepository<Org, Long> {

}
