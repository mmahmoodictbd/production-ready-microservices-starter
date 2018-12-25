package com.chumbok.blog.service;

import com.chumbok.blog.domain.model.Site;
import com.chumbok.blog.domain.model.Tag;
import com.chumbok.blog.domain.repository.SiteRepository;
import com.chumbok.blog.domain.repository.TagRepository;
import com.chumbok.blog.dto.request.TagCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.TagResponse;
import com.chumbok.blog.dto.response.TagsResponse;
import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.SlugUtil;
import com.chumbok.testable.common.UuidUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TagServiceImpl implements TagService {

    private static final String CACHE_KEY_TAGS = "tags";

    private final SiteRepository siteRepository;
    private final TagRepository tagRepository;
    private final SecurityUtil securityUtil;
    private final UuidUtil uuidUtil;
    private final SlugUtil slugUtil;

    @Override
    public TagsResponse getTags(String siteId, Pageable pageable) {

        Page<Tag> tagPage = tagRepository.findAllByCreatedBy(pageable, getLoggedInUsername());

        long totalElements = tagPage.getTotalElements();
        int totalPage = tagPage.getTotalPages();
        int size = tagPage.getSize();
        int page = tagPage.getNumber();

        List<TagResponse> tagResponseList = new ArrayList<>();
        for (Tag tag : tagPage.getContent()) {

            tagResponseList.add(buildTagResponse(tag));
        }

        return TagsResponse.builder()
                .items(tagResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }

    @Override
    public TagResponse getTag(String siteId, String tagId) {

        throwNotFoundExceptionIfSiteNotExist(siteId);
        Tag tag = getTagOrThrowNotFoundException(tagId);

        return buildTagResponse(tag);
    }

    @Override
    @CacheEvict(cacheNames = CACHE_KEY_TAGS, allEntries = true)
    public IdentityResponse createTag(String siteId, TagCreateUpdateRequest tagCreateRequest) {

        Site site = getSiteOrThrowNotFoundException(siteId);

        Tag tag = new Tag();
        tag.setId(uuidUtil.getUuid());

        mapRequestToTag(tagCreateRequest, site, tag);
        tagRepository.save(tag);

        return new IdentityResponse(tag.getId());
    }

    @Override
    public void updateTag(String siteId, String tagId, TagCreateUpdateRequest tagUpdateRequest) {

        Site site = getSiteOrThrowNotFoundException(siteId);
        Tag tag = getTagOrThrowNotFoundException(tagId);

        mapRequestToTag(tagUpdateRequest, site, tag);
        tagRepository.save(tag);

    }
    
    private void mapRequestToTag(TagCreateUpdateRequest tagCreateRequest, Site site, Tag tag) {
        tag.setSite(site);
        tag.setName(tagCreateRequest.getName());

        if (tagCreateRequest.getSlug() == null || tagCreateRequest.getSlug().trim().length() == 0) {
            tag.setSlug(slugUtil.toSlug(tagCreateRequest.getName()));
        } else {
            tag.setSlug(tagCreateRequest.getSlug());
        }

        tag.setAdditionalProperties(tagCreateRequest.getAdditionalProperties());
    }

    @Override
    public void deleteTag(String siteId, String tagId) {

        Site site = getSiteOrThrowNotFoundException(siteId);

        // TODO: Delete posts tags listing.
        siteRepository.delete(site);
    }

    private Site getSiteOrThrowNotFoundException(String siteId) {

        Optional<Site> siteOptional = siteRepository.findByIdAndCreatedBy(siteId, getLoggedInUsername());

        if (!siteOptional.isPresent()) {
            throw new ResourceNotFoundException("Site not found.");
        }

        return siteOptional.get();
    }

    private void throwNotFoundExceptionIfSiteNotExist(String siteId) {
        getSiteOrThrowNotFoundException(siteId);
    }

    private Tag getTagOrThrowNotFoundException(String tagId) {

        Optional<Tag> tagOptional = tagRepository.findByIdAndCreatedBy(tagId, getLoggedInUsername());

        if (!tagOptional.isPresent()) {
            throw new ResourceNotFoundException("Tag not found.");
        }

        return tagOptional.get();
    }

    private TagResponse buildTagResponse(Tag tag) {

        return TagResponse.builder()
                .id(tag.getId())
                .site(tag.getSite().getName())
                .name(tag.getName())
                .slug(tag.getSlug())
                .additionalProperties(tag.getAdditionalProperties())
                .build();
    }

    private String getLoggedInUsername() {
        return securityUtil.getAuthenticatedUser().get().getUsername();
    }

}
