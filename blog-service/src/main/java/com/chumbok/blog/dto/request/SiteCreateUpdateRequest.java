package com.chumbok.blog.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
public class SiteCreateUpdateRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String description;

    @NotNull
    @Size(min = 1, max = 255)
    private String siteUrl;

    @NotNull
    @Size(min = 1, max = 255)
    private String homeUrl;

    private Map<String, String> additionalProperties;
}
