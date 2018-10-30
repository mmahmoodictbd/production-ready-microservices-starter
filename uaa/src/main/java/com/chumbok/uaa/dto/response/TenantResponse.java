package com.chumbok.uaa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TenantResponse {

    private String id;
    private String name;
    private String orgId;
}
