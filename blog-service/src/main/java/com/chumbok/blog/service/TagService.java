package com.chumbok.blog.service;

import com.chumbok.blog.dto.request.TagCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.TagResponse;
import com.chumbok.blog.dto.response.TagsResponse;
import org.springframework.data.domain.Pageable;

public interface TagService {

    TagsResponse getTags(String siteId, Pageable pageable);

    TagResponse getTag(String siteId, String tagId);

    IdentityResponse createTag(String siteId, TagCreateUpdateRequest tagCreateRequest);

    void updateTag(String siteId, String tagId, TagCreateUpdateRequest tagUpdateRequest);

    void deleteTag(String siteId, String tagId);
}
