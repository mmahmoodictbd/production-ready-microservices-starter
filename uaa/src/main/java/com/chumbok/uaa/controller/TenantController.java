package com.chumbok.uaa.controller;

import com.chumbok.uaa.dto.request.TenantCreateUpdateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.dto.response.TenantResponse;
import com.chumbok.uaa.dto.response.TenantsResponse;
import com.chumbok.uaa.exception.presentation.ValidationException;
import com.chumbok.uaa.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * Handles tenants related APIs.
 */
@Slf4j
@RestController
@RequestMapping("/orgs/{orgId}/tenants")
public class TenantController {

    private final TenantService tenantService;

    /**
     * Construct TenantController with TenantService.
     *
     * @param tenantService the tenant service
     */
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Get all tenant page.
     *
     * @param orgId    the org id
     * @param pageable the pageable
     * @return the tenants response
     */
    @GetMapping
    public TenantsResponse tenantList(@PathVariable String orgId, @PageableDefault(size = 10) Pageable pageable) {
        return tenantService.getTenants(orgId, pageable);
    }

    /**
     * Get tenant by id.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @return TenantResponse tenant response
     */
    @GetMapping("/{tenantId}")
    public TenantResponse tenantById(@PathVariable String orgId, @PathVariable String tenantId) {
        return tenantService.getTenant(orgId, tenantId);
    }

    /**
     * Create new tenant.
     *
     * @param orgId                     the org id
     * @param tenantCreateUpdateRequest the tenant create update request
     * @param bindingResult             the binding result
     * @return Id of created Tenant.
     */
    @PostMapping
    public ResponseEntity<IdentityResponse> createTenant(
            @PathVariable String orgId,
            @RequestBody @Valid TenantCreateUpdateRequest tenantCreateUpdateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(tenantService.create(orgId, tenantCreateUpdateRequest), HttpStatus.CREATED);
    }

    /**
     * Delete tenant by id with all tenants and users.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @return the response entity
     */
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Void> deleteTenant(@PathVariable String orgId, @PathVariable String tenantId) {

        tenantService.delete(orgId, tenantId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
