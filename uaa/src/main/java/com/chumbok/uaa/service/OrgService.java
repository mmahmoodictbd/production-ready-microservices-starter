package com.chumbok.uaa.service;

import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.UuidUtil;
import com.chumbok.uaa.domain.model.Org;
import com.chumbok.uaa.domain.repository.OrgRepository;
import com.chumbok.uaa.dto.request.OrgCreateUpdateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.dto.response.OrgResponse;
import com.chumbok.uaa.dto.response.OrgsResponse;
import com.chumbok.uaa.exception.presentation.ResourceNotFoundException;
import com.chumbok.uaa.exception.presentation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrgService {

    private final UuidUtil uuidUtil;
    private final SecurityUtil securityUtil;
    private final OrgRepository orgRepository;

    public OrgService(OrgRepository orgRepository, UuidUtil uuidUtil, SecurityUtil securityUtil) {
        this.uuidUtil = uuidUtil;
        this.securityUtil = securityUtil;
        this.orgRepository = orgRepository;
    }

    @Secured("ROLE_SUPERADMIN")
    public OrgsResponse getOrgs(Pageable pageable) {

        Page<Org> orgPage = orgRepository.findAll(pageable);

        long totalElements = orgPage.getTotalElements();
        int totalPage = orgPage.getTotalPages();
        int size = orgPage.getSize();
        int page = orgPage.getNumber();

        List<OrgResponse> orgResponseList = new ArrayList<>();
        for (Org org : orgPage.getContent()) {
            orgResponseList.add(new OrgResponse(org.getId(), org.getOrg()));
        }

        return OrgsResponse.builder()
                .items(orgResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }

    @Secured("ROLE_SUPERADMIN")
    public OrgResponse getOrg(String id) {

        Org org = getOrgThrowIfNotFound(id);
        return OrgResponse.builder().id(org.getId()).name(org.getOrg()).build();
    }

    @Secured("ROLE_SUPERADMIN")
    public IdentityResponse create(OrgCreateUpdateRequest orgCreateUpdateRequest) {

        if (orgRepository.existsByOrg(orgCreateUpdateRequest.getName())) {
            throw new ValidationException("Org '" + orgCreateUpdateRequest.getName() + "' is already exist.");
        }

        String uuid = uuidUtil.getUuid();
        Org org = new Org();
        org.setId(uuid);
        org.setOrg(orgCreateUpdateRequest.getName());
        orgRepository.saveAndFlush(org);
        return new IdentityResponse(uuid);
    }

    @Secured("ROLE_SUPERADMIN")
    public void delete(String id) {

        // TODO: Delete all tenants and their users.
        Org org = getOrgThrowIfNotFound(id);
        orgRepository.deleteById(id);
    }

    private Org getOrgThrowIfNotFound(String id) {

        Optional<Org> orgOptional = orgRepository.findById(id);

        if (!orgOptional.isPresent()) {
            throw new ResourceNotFoundException("Org not found.");
        }

        return orgOptional.get();
    }
}
