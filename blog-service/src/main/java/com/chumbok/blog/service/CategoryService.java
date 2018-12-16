package com.chumbok.blog.service;

import com.chumbok.blog.dto.request.CategoryCreateUpdateRequest;
import com.chumbok.blog.dto.response.CategoriesResponse;
import com.chumbok.blog.dto.response.CategoryResponse;
import com.chumbok.blog.dto.response.IdentityResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoriesResponse getCategories(String siteId, Pageable pageable);

    CategoryResponse getCategory(String siteId, String categoryId);

    IdentityResponse createCategory(String siteId, CategoryCreateUpdateRequest categoryCreateRequest);

    void updateCategory(String siteId, String categoryId, CategoryCreateUpdateRequest categoryUpdateRequest);

    void deleteCategory(String siteId, String categoryId);
}
