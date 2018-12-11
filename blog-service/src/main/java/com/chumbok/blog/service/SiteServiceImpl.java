package com.chumbok.blog.service;

import com.chumbok.blog.domain.model.Site;
import com.chumbok.blog.domain.repository.SiteRepository;
import com.chumbok.blog.dto.request.SiteCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.SiteResponse;
import com.chumbok.blog.dto.response.SitesResponse;
import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.UuidUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class SiteServiceImpl implements SiteService {

    private static final String CACHE_KEY_SITE = "site";

    private final SiteRepository siteRepository;
    private final SecurityUtil securityUtil;
    private final UuidUtil uuidUtil;
    private final ModelMapper modelMapper;

    @Override
    public SitesResponse getSites(Pageable pageable) {

        Page<Site> sitePage = siteRepository.findAllByCreatedBy(pageable, getLoggedInUsername());

        long totalElements = sitePage.getTotalElements();
        int totalPage = sitePage.getTotalPages();
        int size = sitePage.getSize();
        int page = sitePage.getNumber();

        List<SiteResponse> siteResponseList = new ArrayList<>();
        for (Site site : sitePage.getContent()) {
            siteResponseList.add(modelMapper.map(site, SiteResponse.class));
        }

        return SitesResponse.builder()
                .items(siteResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();

    }

    @Override
    @Cacheable(CACHE_KEY_SITE)
    public SiteResponse getSite(String id) {

        Optional<Site> site = findSite(id);
        throwExceptionWhenSiteNotFound(site);
        return modelMapper.map(site.get(), SiteResponse.class);
    }

    @Override
    @CacheEvict(value = CACHE_KEY_SITE, allEntries = true)
    public IdentityResponse createSite(SiteCreateUpdateRequest siteCreateRequest) {

        String id = uuidUtil.getUuid();
        Site site = new Site();
        site.setId(id);
        modelMapper.map(siteCreateRequest, site);
        siteRepository.save(site);
        return new IdentityResponse(id);
    }

    @Override
    public void updateSite(String id, SiteCreateUpdateRequest siteUpdateRequest) {

        Optional<Site> siteOptional = findSite(id);
        throwExceptionWhenSiteNotFound(siteOptional);

        Site site = siteOptional.get();
        modelMapper.map(siteUpdateRequest, site);
        siteRepository.save(site);
    }

    @Override
    public void deleteSite(String id) {

        Optional<Site> siteOptional = findSite(id);
        throwExceptionWhenSiteNotFound(siteOptional);

        // TODO: Delete tags, categories and posts.
        siteRepository.delete(siteOptional.get());
    }


    private void throwExceptionWhenSiteNotFound(Optional<Site> site) {
        if (!site.isPresent()) {
            throw new ResourceNotFoundException("Site not found.");
        }
    }

    private Optional<Site> findSite(String id) {
        return siteRepository.findByIdAndCreatedBy(id, getLoggedInUsername());
    }

    private String getLoggedInUsername() {
        return securityUtil.getAuthenticatedUser().get().getUsername();
    }


}
