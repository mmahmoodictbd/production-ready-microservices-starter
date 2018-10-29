package com.chumbok.uaa.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class OrgCreateUpdateRequest {

    @Size(min = 1, max = 255)
    private String name;
}
