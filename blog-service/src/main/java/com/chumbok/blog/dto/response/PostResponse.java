package com.chumbok.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PostResponse {

    private String id;

    private String title;

    private String content;

    private String summary;

    private String categories;

    private String tags;

    private String permalink;

    private String featureImageLink;

    private String publishDate;

    private Map<String, String> additionalProperties;

    private String createdAt;

    private String updatedAt;
}
