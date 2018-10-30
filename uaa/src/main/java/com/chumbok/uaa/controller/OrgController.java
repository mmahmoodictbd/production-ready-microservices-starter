package com.chumbok.uaa.controller;

import com.chumbok.uaa.dto.request.OrgCreateUpdateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.dto.response.OrgResponse;
import com.chumbok.uaa.dto.response.OrgsResponse;
import com.chumbok.uaa.exception.presentation.ValidationException;
import com.chumbok.uaa.service.OrgService;
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
 * Handles orgs related APIs.
 */
@Slf4j
@RestController
@RequestMapping("/orgs")
public class OrgController {

    private final OrgService orgService;

    /**
     * Construct OrgController with OrgService.
     *
     * @param orgService the org service
     */
    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }

    /**
     * Get all org page.
     *
     * @param pageable the pageable
     * @return the orgs response
     */
    @GetMapping
    public OrgsResponse orgList(@PageableDefault(size = 10) Pageable pageable) {
        return orgService.getOrgs(pageable);
    }

    /**
     * Get org by id.
     *
     * @param id the id
     * @return OrgResponse org response
     */
    @GetMapping("/{id}")
    public OrgResponse orgById(@PathVariable String id) {
        return orgService.getOrg(id);
    }

    /**
     * Create new org.
     *
     * @param orgCreateUpdateRequest the org create update request
     * @param bindingResult          the binding result
     * @return Id of created Org.
     */
    @PostMapping
    public ResponseEntity<IdentityResponse> createOrg(
            @RequestBody @Valid OrgCreateUpdateRequest orgCreateUpdateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(orgService.create(orgCreateUpdateRequest), HttpStatus.CREATED);
    }

    /**
     * Delete org by id with all tenants and users.
     *
     * @param id the id
     * @return the response entity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrg(@PathVariable String id) {

        orgService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
