package com.chumbok.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class PostsResponse {

    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<PostResponse> items;
}
