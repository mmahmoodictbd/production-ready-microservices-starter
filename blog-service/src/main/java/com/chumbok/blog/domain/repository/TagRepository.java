package com.chumbok.blog.domain.repository;

import com.chumbok.blog.domain.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("select tag  from Tag tag where tag.name = ?1")
    Tag findByName(String name);

    Optional<Tag> findByIdAndCreatedBy(String id, String owner);

    Page<Tag> findAllByCreatedBy(Pageable pageable, String owner);
}
