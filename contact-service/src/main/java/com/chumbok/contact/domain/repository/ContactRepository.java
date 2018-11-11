package com.chumbok.contact.domain.repository;

import com.chumbok.contact.domain.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Contact multi-tenant repository.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    Page<Contact> findAllByCreatedBy(String ownerUsername, Pageable pageable);

    Optional<Contact> findByIdAndCreatedBy(String id, String ownerUsername);
}
