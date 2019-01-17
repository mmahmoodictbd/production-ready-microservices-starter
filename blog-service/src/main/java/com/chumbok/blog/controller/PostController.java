package com.chumbok.blog.controller;

import com.chumbok.blog.dto.request.PostCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.PostResponse;
import com.chumbok.blog.dto.response.PostsResponse;
import com.chumbok.blog.service.PostService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/sites/{siteId}/posts")
public class PostController {

    private PostService postService;

    @GetMapping
    public PostsResponse postList(@PathVariable String siteId,
                                  @RequestParam(required = false) String status,
                                  @PageableDefault Pageable pageable) {
        return postService.getPosts(siteId, status, pageable);
    }

    @PostMapping
    public ResponseEntity<IdentityResponse> createPost(
            @PathVariable String siteId,
            @RequestBody @Valid PostCreateUpdateRequest postCreateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(postService.createPost(siteId, postCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public PostResponse postById(@PathVariable String siteId, @PathVariable String id) {
        return postService.getPost(siteId, id);
    }

    @PutMapping("/{id}")
    public void updatePost(@PathVariable String siteId, @PathVariable String id,
                           @RequestBody @Valid PostCreateUpdateRequest postUpdateRequest,
                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        postService.updatePost(siteId, id, postUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String siteId, @PathVariable String id) {

        postService.deletePost(siteId, id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
