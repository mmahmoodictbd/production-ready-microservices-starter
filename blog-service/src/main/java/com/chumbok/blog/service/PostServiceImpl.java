package com.chumbok.blog.service;

import com.chumbok.blog.domain.model.Category;
import com.chumbok.blog.domain.model.DraftPost;
import com.chumbok.blog.domain.model.PublishedPost;
import com.chumbok.blog.domain.model.Tag;
import com.chumbok.blog.domain.repository.DraftPostRepository;
import com.chumbok.blog.domain.repository.PublishedPostRepository;
import com.chumbok.blog.dto.request.PostCreateUpdateRequest;
import com.chumbok.blog.dto.response.IdentityResponse;
import com.chumbok.blog.dto.response.PostResponse;
import com.chumbok.blog.dto.response.PostsResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private static DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

//    private DraftPostService draftPostService;
//    private PublishPostService publishPostService;

    private PublishedPostRepository publishedPostRepository;
    private DraftPostRepository draftPostRepository;

    @Override
    public PostsResponse getPosts(String siteId, String status, Pageable pageable) {

        List<PostResponse> postResponseList = new ArrayList<>();

        long totalElements = 0;
        int totalPage = 0;
        int size = 0;
        int page = 0;

        if (status == null || status.trim().length() == 0 || "published".equalsIgnoreCase(status)) {

            Page<PublishedPost> publishedPostsPage = publishedPostRepository.findAll(pageable);
            for (PublishedPost post : publishedPostsPage.getContent()) {
                postResponseList.add(buildPostResponse(post));
            }

            totalElements = publishedPostsPage.getTotalElements();
            totalPage = publishedPostsPage.getTotalPages();
            size = publishedPostsPage.getSize();
            page = publishedPostsPage.getNumber();

        } else if ("draft".equalsIgnoreCase(status)) {

            Page<DraftPost> draftPostsPage = draftPostRepository.findAll(pageable);
            for (DraftPost post : draftPostsPage.getContent()) {
                postResponseList.add(buildPostResponse(post));
            }

            totalElements = draftPostsPage.getTotalElements();
            totalPage = draftPostsPage.getTotalPages();
            size = draftPostsPage.getSize();
            page = draftPostsPage.getNumber();

        }

        return PostsResponse.builder()
                .items(postResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }

    private PostResponse buildPostResponse(PublishedPost post) {

        PostResponse postResponse = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .summary(post.getSummary())
                .categories(convertCategorySetToCommaSeparatedString(post.getCategories()))
                .tags(convertTagSetToCommaSeparatedString(post.getTags()))
                .permalink(post.getPermalink())
                .featureImageLink(post.getFeatureImageLink())
                .publishDate(df.format(post.getPublishDate()))
                .additionalProperties(post.getAdditionalProperties())
                .createdAt(df.format(post.getCreatedAt()))
                .updatedAt(df.format(post.getUpdatedAt()))
                .build();

        return postResponse;
    }

    private PostResponse buildPostResponse(DraftPost post) {

        PostResponse postResponse = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .summary(post.getSummary())
                .categories(convertCategorySetToCommaSeparatedString(post.getCategories()))
                .tags(convertTagSetToCommaSeparatedString(post.getTags()))
                .permalink(post.getPermalink())
                .featureImageLink(post.getFeatureImageLink())
                .additionalProperties(post.getAdditionalProperties())
                .createdAt(df.format(post.getCreatedAt()))
                .updatedAt(df.format(post.getUpdatedAt()))
                .build();

        return postResponse;
    }

    private String convertCategorySetToCommaSeparatedString(Set<Category> categories) {
        return StringUtils.join(categories.stream().map(Category::getName).collect(Collectors.toList()), ",");
    }

    private String convertTagSetToCommaSeparatedString(Set<Tag> tags) {
        return StringUtils.join(tags.stream().map(Tag::getName).collect(Collectors.toList()), ",");
    }

    @Override
    public IdentityResponse createPost(String siteId, PostCreateUpdateRequest postCreateRequest) {
        return null;
    }

    @Override
    public PostResponse getPost(String siteId, String id) {
        return null;
    }

    @Override
    public void updatePost(String siteId, String id, PostCreateUpdateRequest postUpdateRequest) {

    }

    @Override
    public void deletePost(String siteId, String id) {

    }
}
