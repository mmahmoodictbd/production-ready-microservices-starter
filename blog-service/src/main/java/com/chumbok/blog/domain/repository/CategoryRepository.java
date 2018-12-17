package com.chumbok.blog.domain.repository;

import com.chumbok.blog.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select category  from Category category where category.name = ?1")
    Category findByName(String name);

    List<Category> findCategoriesByParent(String parent);

    Optional<Category> findByIdAndCreatedBy(String id, String owner);

    Page<Category> findAllByCreatedBy(Pageable pageable, String owner);
}
