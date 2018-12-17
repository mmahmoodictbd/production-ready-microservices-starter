package com.chumbok.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CategoryResponse {

    private String id;

    private String site;

    private String name;

    private String slug;

    private String parent;

    private Map<String, String> additionalProperties;
}
