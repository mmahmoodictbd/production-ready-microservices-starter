package com.chumbok.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TagResponse {

    private String id;

    private String site;

    private String name;

    private String slug;

    private Map<String, String> additionalProperties;
}
