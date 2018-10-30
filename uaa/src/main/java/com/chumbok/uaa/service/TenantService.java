package com.chumbok.uaa.service;

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
import com.chumbok.uaa.exception.presentation.ResourceNotFoundException;
import com.chumbok.uaa.exception.presentation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TenantService {

    private final OrgRepository orgRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final UuidUtil uuidUtil;

    public TenantService(OrgRepository orgRepository, TenantRepository tenantRepository,
                         UserRepository userRepository, UuidUtil uuidUtil) {
        this.orgRepository = orgRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.uuidUtil = uuidUtil;
    }

    @Secured("ROLE_SUPERADMIN")
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

    @Secured("ROLE_SUPERADMIN")
    public TenantResponse getTenant(String orgId, String tenantId) {

        Optional<Tenant> tenantOptional = tenantRepository.findByOrgIdAndId(orgId, tenantId);
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

    @Secured("ROLE_SUPERADMIN")
    public IdentityResponse create(String orgId, TenantCreateUpdateRequest tenantCreateUpdateRequest) {

        Optional<Org> orgOptional = orgRepository.findById(orgId);

        if (!orgOptional.isPresent()) {
            throw new ValidationException("Org not found.");
        }

        if (tenantRepository.existsByOrgIdAndTenant(orgId, tenantCreateUpdateRequest.getName())) {
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

    @Secured("ROLE_SUPERADMIN")
    public void delete(String orgId, String tenantId) {

        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (!tenantOptional.isPresent()) {
            throw new ResourceNotFoundException("Tenant not found.");
        }

        userRepository.deleteAllByOrgIdAndTenantId(orgId, tenantId);
        tenantRepository.deleteById(tenantId);
    }

}
