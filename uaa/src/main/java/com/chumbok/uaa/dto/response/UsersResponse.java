package com.chumbok.uaa.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UsersResponse {

    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<UserResponse> items;
}
