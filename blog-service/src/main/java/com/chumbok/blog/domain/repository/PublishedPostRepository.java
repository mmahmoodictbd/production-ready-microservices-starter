package com.chumbok.blog.domain.repository;

import com.chumbok.blog.domain.model.PublishedPost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishedPostRepository extends PagingAndSortingRepository<PublishedPost, Long> {

    @Query("select pp.permalink  from PublishedPost pp where pp.id = ?1")
    String getPermalinkById(Long id);

}
