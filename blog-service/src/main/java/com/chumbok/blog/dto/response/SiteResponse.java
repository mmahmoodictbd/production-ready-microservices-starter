package com.chumbok.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SiteResponse {

    private String id;

    private String name;

    private String description;

    private String siteUrl;

    private String homeUrl;

    private Map<String, String> additionalProperties;
}
