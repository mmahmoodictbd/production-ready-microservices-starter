package com.chumbok.blog.service;

import com.chumbok.blog.dto.request.PostCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.PostResponse;
import com.chumbok.blog.dto.response.PostsResponse;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostsResponse getPosts(String siteId, String status, Pageable pageable);

    IdentityResponse createPost(String siteId, PostCreateUpdateRequest postCreateRequest);

    PostResponse getPost(String siteId, String id);

    void updatePost(String siteId, String id, PostCreateUpdateRequest postUpdateRequest);

    void deletePost(String siteId, String id);
}
