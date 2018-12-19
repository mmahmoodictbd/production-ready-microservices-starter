package com.chumbok.blog.controller;

import com.chumbok.blog.dto.request.CategoryCreateUpdateRequest;
import com.chumbok.blog.dto.response.CategoriesResponse;
import com.chumbok.blog.dto.response.CategoryResponse;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.service.CategoryService;
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
@RequestMapping("/sites/{siteId}/categories")
public class CategoryController {

    private CategoryService categoryService;

    @GetMapping
    public CategoriesResponse categoryList(@PathVariable String siteId, @PageableDefault Pageable pageable) {
        return categoryService.getCategories(siteId, pageable);
    }

    @PostMapping
    public ResponseEntity<IdentityResponse> createCategory(
            @PathVariable String siteId,
            @RequestBody @Valid CategoryCreateUpdateRequest categoryCreateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(categoryService.createCategory(siteId, categoryCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public CategoryResponse categoryById(@PathVariable String siteId, @PathVariable String id) {
        return categoryService.getCategory(siteId, id);
    }

    @PutMapping("/{id}")
    public void updateCategory(@PathVariable String siteId, @PathVariable String id,
                               @RequestBody @Valid CategoryCreateUpdateRequest categoryUpdateRequest,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        categoryService.updateCategory(siteId, id, categoryUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String siteId, @PathVariable String id) {

        categoryService.deleteCategory(siteId, id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
