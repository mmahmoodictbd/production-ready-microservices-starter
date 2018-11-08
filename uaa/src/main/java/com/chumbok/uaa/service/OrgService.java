package com.chumbok.uaa.service;

import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.exception.presentation.ValidationException;
import com.chumbok.testable.common.UuidUtil;
import com.chumbok.uaa.domain.model.Org;
import com.chumbok.uaa.domain.repository.OrgRepository;
import com.chumbok.uaa.domain.repository.TenantRepository;
import com.chumbok.uaa.domain.repository.UserRepository;
import com.chumbok.uaa.dto.request.OrgCreateUpdateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.dto.response.OrgResponse;
import com.chumbok.uaa.dto.response.OrgsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.chumbok.uaa.security.DefaultSecurityRoleConstants.ROLE_SUPERADMIN;

/**
 * Handles ORG related APIs
 */
@Service
public class OrgService {

    private final OrgRepository orgRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final UuidUtil uuidUtil;

    /**
     * Instantiates a new Org service.
     *
     * @param orgRepository    the org repository
     * @param tenantRepository the tenant repository
     * @param userRepository   the user repository
     * @param uuidUtil         the uuid util
     */
    public OrgService(OrgRepository orgRepository, TenantRepository tenantRepository,
                      UserRepository userRepository, UuidUtil uuidUtil) {
        this.orgRepository = orgRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.uuidUtil = uuidUtil;
    }

    /**
     * Gets pageable orgs.
     *
     * @param pageable the pageable
     * @return the orgs page
     */
    @Secured(ROLE_SUPERADMIN)
    @Transactional(readOnly = true)
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

    /**
     * Gets org.
     *
     * @param orgId the orgId
     * @return the org
     */
    @Secured(ROLE_SUPERADMIN)
    @Transactional(readOnly = true)
    public OrgResponse getOrg(String orgId) {

        Org org = getOrgOrThrowNotFoundException(orgId);
        return OrgResponse.builder().id(org.getId()).name(org.getOrg()).build();
    }

    /**
     * Create Org.
     *
     * @param orgCreateUpdateRequest the org create update request
     * @return the uuid
     */
    @Secured(ROLE_SUPERADMIN)
    public IdentityResponse createOrg(OrgCreateUpdateRequest orgCreateUpdateRequest) {

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

    /**
     * Delete Org.
     *
     * @param orgId the orgId
     */
    @Secured(ROLE_SUPERADMIN)
    public void deleteOrg(String orgId) {

        Org org = getOrgOrThrowNotFoundException(orgId);

        List<String> tenantIdList = tenantRepository.findTenantIdsByOrgId(org.getId());
        for (String tenantId : tenantIdList) {
            userRepository.deleteAllByOrgIdAndTenantId(orgId, tenantId);
        }

        tenantRepository.deleteAllByOrgId(orgId);
        orgRepository.deleteById(orgId);
    }

    private Org getOrgOrThrowNotFoundException(String orgId) {

        Optional<Org> orgOptional = orgRepository.findById(orgId);

        if (!orgOptional.isPresent()) {
            throw new ResourceNotFoundException("Org not found.");
        }

        return orgOptional.get();
    }
}
