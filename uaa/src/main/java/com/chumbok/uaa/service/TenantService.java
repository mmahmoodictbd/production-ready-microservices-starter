package com.chumbok.uaa.service;

import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.exception.presentation.ValidationException;
import com.chumbok.testable.common.UuidUtil;
import com.chumbok.uaa.domain.model.Org;
import com.chumbok.uaa.domain.model.Tenant;
import com.chumbok.uaa.domain.repository.OrgRepository;
import com.chumbok.uaa.domain.repository.TenantRepository;
import com.chumbok.uaa.domain.repository.UserRepository;
import com.chumbok.uaa.dto.request.TenantCreateUpdateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.dto.response.TenantResponse;
import com.chumbok.uaa.dto.response.TenantsResponse;
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
 * Handles TENANT related APIs
 */
@Service
public class TenantService {

    private final OrgRepository orgRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final UuidUtil uuidUtil;

    /**
     * Instantiates a new Tenant service.
     *
     * @param orgRepository    the org repository
     * @param tenantRepository the tenant repository
     * @param userRepository   the user repository
     * @param uuidUtil         the uuid util
     */
    public TenantService(OrgRepository orgRepository, TenantRepository tenantRepository,
                         UserRepository userRepository, UuidUtil uuidUtil) {
        this.orgRepository = orgRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.uuidUtil = uuidUtil;
    }

    /**
     * Gets tenants page.
     *
     * @param orgId    the org id
     * @param pageable the pageable
     * @return the tenants
     */
    @Secured(ROLE_SUPERADMIN)
    @Transactional(readOnly = true)
    public TenantsResponse getTenants(String orgId, Pageable pageable) {

        Page<Tenant> tenantPage = tenantRepository.findAllByOrgId(orgId, pageable);

        long totalElements = tenantPage.getTotalElements();
        int totalPage = tenantPage.getTotalPages();
        int size = tenantPage.getSize();
        int page = tenantPage.getNumber();

        List<TenantResponse> tenantResponseList = new ArrayList<>();
        for (Tenant tenant : tenantPage.getContent()) {
            tenantResponseList.add(TenantResponse.builder()
                    .id(tenant.getId())
                    .name(tenant.getTenant())
                    .orgId(tenant.getOrg().getId())
                    .build());
        }

        return TenantsResponse.builder()
                .items(tenantResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }

    /**
     * Gets tenant.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @return the tenant
     */
    @Secured(ROLE_SUPERADMIN)
    @Transactional(readOnly = true)
    public TenantResponse getTenant(String orgId, String tenantId) {

        Optional<Org> orgOptional = orgRepository.findById(orgId);

        if (!orgOptional.isPresent()) {
            throw new ResourceNotFoundException("Org not found.");
        }

        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (!tenantOptional.isPresent()) {
            throw new ResourceNotFoundException("Tenant not found.");
        }

        Tenant tenant = tenantOptional.get();
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getTenant())
                .orgId(tenant.getOrg().getId())
                .build();
    }

    /**
     * Create tenant.
     *
     * @param orgId                     the org id
     * @param tenantCreateUpdateRequest the tenant create update request
     * @return the identity response
     */
    @Secured(ROLE_SUPERADMIN)
    public IdentityResponse createTenant(String orgId, TenantCreateUpdateRequest tenantCreateUpdateRequest) {

        Optional<Org> orgOptional = orgRepository.findById(orgId);

        if (!orgOptional.isPresent()) {
            throw new ResourceNotFoundException("Org not found.");
        }

        if (tenantRepository.isTenantExist(orgId, tenantCreateUpdateRequest.getName())) {
            throw new ValidationException("Tenant '" + tenantCreateUpdateRequest.getName() + "' is already exist.");
        }

        String uuid = uuidUtil.getUuid();
        Tenant tenant = new Tenant();
        tenant.setId(uuid);
        tenant.setTenant(tenantCreateUpdateRequest.getName());
        tenant.setOrg(orgOptional.get());
        tenantRepository.saveAndFlush(tenant);
        return new IdentityResponse(uuid);
    }

    /**
     * Delete tenant.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     */
    @Secured(ROLE_SUPERADMIN)
    public void deleteTenant(String orgId, String tenantId) {

        Optional<Org> orgOptional = orgRepository.findById(orgId);

        if (!orgOptional.isPresent()) {
            throw new ResourceNotFoundException("Org not found.");
        }

        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (!tenantOptional.isPresent()) {
            throw new ResourceNotFoundException("Tenant not found.");
        }

        userRepository.deleteAllByOrgIdAndTenantId(orgId, tenantId);
        tenantRepository.deleteById(tenantId);
    }

}
