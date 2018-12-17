package com.chumbok.blog.service;

import com.chumbok.blog.domain.model.Category;
import com.chumbok.blog.domain.model.Site;
import com.chumbok.blog.domain.repository.CategoryRepository;
import com.chumbok.blog.domain.repository.SiteRepository;
import com.chumbok.blog.dto.request.CategoryCreateUpdateRequest;
import com.chumbok.blog.dto.response.CategoriesResponse;
import com.chumbok.blog.dto.response.CategoryResponse;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.SlugUtil;
import com.chumbok.testable.common.UuidUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String COMMA = ",";
    private static final String CACHE_KEY_CATEGORIES = "categories";

    private final SiteRepository siteRepository;
    private final CategoryRepository categoryRepository;
    private final SecurityUtil securityUtil;
    private final UuidUtil uuidUtil;
    private final SlugUtil slugUtil;

    @Override
    public CategoriesResponse getCategories(String siteId, Pageable pageable) {

        Page<Category> categoryPage = categoryRepository.findAllByCreatedBy(pageable, getLoggedInUsername());

        long totalElements = categoryPage.getTotalElements();
        int totalPage = categoryPage.getTotalPages();
        int size = categoryPage.getSize();
        int page = categoryPage.getNumber();

        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        for (Category category : categoryPage.getContent()) {

            categoryResponseList.add(buildCategoryResponse(category));
        }

        return CategoriesResponse.builder()
                .items(categoryResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }

    @Override
    public CategoryResponse getCategory(String siteId, String categoryId) {

        throwNotFoundExceptionIfSiteNotExist(siteId);
        Category category = getCategoryOrThrowNotFoundException(categoryId);

        return buildCategoryResponse(category);
    }

    @Override
    @CacheEvict(cacheNames = CACHE_KEY_CATEGORIES, allEntries = true)
    public IdentityResponse createCategory(String siteId, CategoryCreateUpdateRequest categoryCreateRequest) {

        Site site = getSiteOrThrowNotFoundException(siteId);

        Category category = new Category();
        category.setId(uuidUtil.getUuid());
        category.setSite(site);
        category.setName(categoryCreateRequest.getName());

        if (categoryCreateRequest.getSlug() == null || categoryCreateRequest.getSlug().trim().length() == 0) {
            category.setSlug(slugUtil.toSlug(categoryCreateRequest.getName()));
        } else {
            category.setSlug(categoryCreateRequest.getSlug());
        }

        category.setParent(categoryCreateRequest.getParent());
        category.setAdditionalProperties(categoryCreateRequest.getAdditionalProperties());
        categoryRepository.save(category);

        return new IdentityResponse(category.getId());
    }

    @Override
    public void updateCategory(String siteId, String categoryId, CategoryCreateUpdateRequest categoryUpdateRequest) {

        Site site = getSiteOrThrowNotFoundException(siteId);
        Category category = getCategoryOrThrowNotFoundException(categoryId);

        category.setSite(site);
        category.setName(categoryUpdateRequest.getName());

        if (categoryUpdateRequest.getSlug() == null || categoryUpdateRequest.getSlug().trim().length() == 0) {
            category.setSlug(slugUtil.toSlug(categoryUpdateRequest.getName()));
        } else {
            category.setSlug(categoryUpdateRequest.getSlug());
        }

        if (category.getName() != null && !categoryUpdateRequest.getName().equals(category.getName())) {
            List<Category> categoriesUsingThisCategoryAsParent =
                    categoryRepository.findCategoriesByParent(category.getName());
            for (Category cat : categoriesUsingThisCategoryAsParent) {
                cat.setParent(categoryUpdateRequest.getName());
                categoryRepository.save(cat);
            }
        }

        category.setParent(categoryUpdateRequest.getParent());
        category.setAdditionalProperties(categoryUpdateRequest.getAdditionalProperties());
        categoryRepository.save(category);

    }

    @Override
    public void deleteCategory(String siteId, String categoryId) {

        Site site = getSiteOrThrowNotFoundException(siteId);

        // TODO: Delete posts category listing.
        siteRepository.delete(site);
    }

    private Site getSiteOrThrowNotFoundException(String siteId) {

        Optional<Site> siteOptional = siteRepository.findByIdAndCreatedBy(siteId, getLoggedInUsername());

        if (!siteOptional.isPresent()) {
            throw new ResourceNotFoundException("Site not found.");
        }

        return siteOptional.get();
    }

    private void throwNotFoundExceptionIfSiteNotExist(String siteId) {
        getSiteOrThrowNotFoundException(siteId);
    }

    private Category getCategoryOrThrowNotFoundException(String categoryId) {

        Optional<Category> categoryOptional = categoryRepository.findByIdAndCreatedBy(categoryId, getLoggedInUsername());

        if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Category not found.");
        }

        return categoryOptional.get();
    }

    private CategoryResponse buildCategoryResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .site(category.getSite().getName())
                .name(category.getName())
                .slug(category.getSlug())
                .parent(category.getParent())
                .additionalProperties(category.getAdditionalProperties())
                .build();
    }

    private String getLoggedInUsername() {
        return securityUtil.getAuthenticatedUser().get().getUsername();
    }

}
