package com.chumbok.blog.service;

import com.chumbok.blog.dto.request.SiteCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.SiteResponse;
import com.chumbok.blog.dto.response.SitesResponse;
import org.springframework.data.domain.Pageable;


public interface SiteService {

    SitesResponse getSites(Pageable pageable);

    SiteResponse getSite(String id);

    IdentityResponse createSite(SiteCreateUpdateRequest siteCreateRequest);

    void updateSite(String id, SiteCreateUpdateRequest siteUpdateRequest);

    void deleteSite(String id);
}
