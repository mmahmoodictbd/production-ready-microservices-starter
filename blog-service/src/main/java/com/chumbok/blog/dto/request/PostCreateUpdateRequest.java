package com.chumbok.blog.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
public class PostCreateUpdateRequest {

    @NotNull
    private String title;

    @NotNull
    private String content;

    private String summary;

    private String categories;

    private String tags;

    private String permalink;

    private String featureImageLink;

    private Map<String, String> additionalProperties;

}
