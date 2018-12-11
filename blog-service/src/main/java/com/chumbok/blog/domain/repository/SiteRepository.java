package com.chumbok.blog.domain.repository;

import com.chumbok.blog.domain.model.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    Page<Site> findAllByCreatedBy(Pageable pageable, String owner);

    Optional<Site> findByIdAndCreatedBy(String id, String owner);
}
