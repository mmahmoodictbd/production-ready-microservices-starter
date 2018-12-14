package com.chumbok.blog.service;

import com.chumbok.blog.dto.request.CategoryCreateUpdateRequest;
import com.chumbok.blog.dto.response.CategoriesResponse;
import com.chumbok.blog.dto.response.CategoryResponse;
import com.chumbok.blog.dto.response.IdentityResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoriesResponse getCategories(Pageable pageable);

    CategoryResponse getCategory(String id);

    IdentityResponse createCategory(CategoryCreateUpdateRequest CategoryCreateRequest);

    void updateCategory(String id, CategoryCreateUpdateRequest CategoryUpdateRequest);

    void deleteCategory(String id);
}
