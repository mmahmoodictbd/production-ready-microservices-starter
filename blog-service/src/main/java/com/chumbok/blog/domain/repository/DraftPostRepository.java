package com.chumbok.blog.domain.repository;

import com.chumbok.blog.domain.model.DraftPost;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DraftPostRepository extends PagingAndSortingRepository<DraftPost, Long> {

}
