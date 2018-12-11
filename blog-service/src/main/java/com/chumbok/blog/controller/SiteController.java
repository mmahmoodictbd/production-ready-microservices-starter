package com.chumbok.blog.controller;

import com.chumbok.blog.dto.request.SiteCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.SiteResponse;
import com.chumbok.blog.dto.response.SitesResponse;
import com.chumbok.blog.service.SiteService;
import com.chumbok.exception.presentation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/sites")
public class SiteController {

    private SiteService siteService;

    @GetMapping
    public SitesResponse siteList(@PageableDefault(size = 10) Pageable pageable) {
        return siteService.getSites(pageable);
    }

    @PostMapping
    public ResponseEntity<IdentityResponse> createSite(
            @RequestBody @Valid SiteCreateUpdateRequest siteCreateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(siteService.createSite(siteCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public SiteResponse siteById(@PathVariable String id) {
        return siteService.getSite(id);
    }

    @PutMapping("/{id}")
    public void updateSite(@PathVariable String id,
                           @RequestBody @Valid SiteCreateUpdateRequest siteUpdateRequest,
                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        siteService.updateSite(id, siteUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable String id) {

        siteService.deleteSite(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
