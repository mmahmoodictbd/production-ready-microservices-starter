package com.chumbok.blog.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
public class TagCreateUpdateRequest {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @Size(min = 1, max = 255)
    private String slug;

    @Size(min = 1, max = 255)
    private String parent;

    private Map<String, String> additionalProperties;
}
