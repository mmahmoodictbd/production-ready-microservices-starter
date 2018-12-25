package com.chumbok.blog.controller;

import com.chumbok.blog.dto.request.TagCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.TagResponse;
import com.chumbok.blog.dto.response.TagsResponse;
import com.chumbok.blog.service.TagService;
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
@RequestMapping("/sites/{siteId}/tags")
public class TagController {

    private TagService tagService;

    @GetMapping
    public TagsResponse tagList(@PathVariable String siteId, @PageableDefault Pageable pageable) {
        return tagService.getTags(siteId, pageable);
    }

    @PostMapping
    public ResponseEntity<IdentityResponse> createTag(
            @PathVariable String siteId,
            @RequestBody @Valid TagCreateUpdateRequest tagCreateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(tagService.createTag(siteId, tagCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public TagResponse tagById(@PathVariable String siteId, @PathVariable String id) {
        return tagService.getTag(siteId, id);
    }

    @PutMapping("/{id}")
    public void updateTag(@PathVariable String siteId, @PathVariable String id,
                          @RequestBody @Valid TagCreateUpdateRequest tagUpdateRequest,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        tagService.updateTag(siteId, id, tagUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String siteId, @PathVariable String id) {

        tagService.deleteTag(siteId, id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
